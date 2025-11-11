package service;

import exception.CursoIncompativelException;
import exception.DiscenteInativoException;
import exception.LimiteMatriculasExcedidoException;
import exception.LivroIndisponivelException;
import exception.SemVagasException;
import model.Matricula;
import model.ReservaLivro;
import repository.IMatriculaRepository;
import repository.IReservaRepository;
import util.GeradorMatricula;
import util.Logger;

import java.time.LocalDate;
import java.util.List;

public class GestaoAcademicaService {
    
    // Dependências injetadas via construtor (DIP)
    private final IMatriculaRepository matriculaRepository;
    private final IReservaRepository reservaRepository;
    private final DisponibilidadeService disponibilidadeService;
    
    public GestaoAcademicaService(
            IMatriculaRepository matriculaRepository,
            IReservaRepository reservaRepository,
            DisponibilidadeService disponibilidadeService) {
        
        this.matriculaRepository = matriculaRepository;
        this.reservaRepository = reservaRepository;
        this.disponibilidadeService = disponibilidadeService;
    }
    
    /**
     * Simula matrícula em disciplina com PERSISTÊNCIA no banco de dados.
     */
    public boolean simularMatricula(String discenteId, String disciplinaId) {
        try {
            // ETAPA 1: Validar regras de negócio (microsserviços + banco)
            disponibilidadeService.validarMatricula(discenteId, disciplinaId);
            
            // ETAPA 2: Verificar duplicidade no banco
            if (matriculaRepository.existeMatricula(discenteId, disciplinaId)) {
                Logger.erro("Discente já matriculado nesta disciplina.");
                return false;
            }
            
            // ETAPA 3: Gerar código único de matrícula
            String codigoMatricula = gerarCodigoMatriculaUnico();
            
            // ETAPA 4: Criar objeto de domínio
            Matricula matricula = Matricula.nova(codigoMatricula, discenteId, disciplinaId, LocalDate.now());
            
            // ETAPA 5: Persistir no banco de dados
            boolean sucesso = matriculaRepository.adicionar(matricula);
            
            if (sucesso) {
                Logger.sucesso("Matrícula realizada: " + codigoMatricula);
                Logger.dica("Use o código " + codigoMatricula + " para consultar ou cancelar esta matrícula.");
                return true;
            } else {
                Logger.erro("Erro ao salvar matrícula no banco de dados.");
                return false;
            }
            
        } catch (DiscenteInativoException e) {
            Logger.erro(e.getMessage());
            return false;
        } catch (CursoIncompativelException e) {
            Logger.erro(e.getMessage());
            return false;
        } catch (LimiteMatriculasExcedidoException e) {
            Logger.erro(e.getMessage());
            return false;
        } catch (SemVagasException e) {
            Logger.erro(e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Logger.erro("Dados inválidos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gera um código único de matrícula baseado na data atual.
     */
    private String gerarCodigoMatriculaUnico() {
        // Usar timestamp como ordem para garantir unicidade
        long timestamp = System.currentTimeMillis();
        int ordem = (int) (timestamp % 9999) + 1; // Entre 1 e 9999
        return GeradorMatricula.gerarComDataAtual(ordem);
    }
    
    /**
     * Cancela matrícula existente no banco em memória.
     */
    public boolean cancelarMatricula(String discenteId, String disciplinaId) {
        // Verificar se matrícula existe no banco
        if (!matriculaRepository.existeMatricula(discenteId, disciplinaId)) {
            Logger.erro("Matrícula não encontrada.");
            return false;
        }
        
        // Remover do banco
        boolean sucesso = matriculaRepository.remover(discenteId, disciplinaId);
        
        if (sucesso) {
            Logger.sucesso("Matrícula cancelada.");
            return true;
        }
        
        return false;
    }
    
    /**
     * Cancela matrícula pelo código da matrícula.
     */
    public boolean cancelarMatriculaPorCodigo(String codigoMatricula) {
        // Remover do banco
        boolean sucesso = matriculaRepository.removerPorCodigo(codigoMatricula);
        
        if (sucesso) {
            Logger.sucesso("Matrícula cancelada: " + codigoMatricula);
            return true;
        } else {
            Logger.erro("Matrícula não encontrada.");
            return false;
        }
    }
    
    /**
     * Simula reserva de livro com PERSISTÊNCIA no banco de dados.
     */
    public boolean simularReservaLivro(String discenteId, String livroId) {
        try {
            // ETAPA 1: Validar disponibilidade (microsserviço + banco)
            disponibilidadeService.validarReservaLivro(livroId);
            
            // ETAPA 2: Verificar se discente já reservou este livro
            if (reservaRepository.existeReserva(discenteId, livroId)) {
                Logger.erro("Você já reservou este livro.");
                return false;
            }
            
            // ETAPA 3: Criar objeto de domínio
            ReservaLivro reserva = ReservaLivro.nova(discenteId, livroId, LocalDate.now());
            
            // ETAPA 4: Persistir no banco de dados
            boolean sucesso = reservaRepository.adicionar(reserva);
            
            if (sucesso) {
                Logger.sucesso("Livro reservado.");
                return true;
            } else {
                Logger.erro("Erro ao registrar reserva.");
                return false;
            }
            
        } catch (LivroIndisponivelException e) {
            Logger.erro(e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Logger.erro("Dados inválidos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cancela reserva de livro existente no banco em memória.
     */
    public boolean cancelarReservaLivro(String discenteId, String livroId) {
        // Verificar se reserva existe no banco
        if (!reservaRepository.existeReserva(discenteId, livroId)) {
            Logger.erro("Reserva não encontrada.");
            return false;
        }
        
        // Remover do banco
        boolean sucesso = reservaRepository.remover(discenteId, livroId);
        
        if (sucesso) {
            Logger.sucesso("Reserva cancelada.");
            return true;
        }
        
        return false;
    }
    
    /**
     * Lista todas as matrículas de um discente.
     */
    public List<Matricula> listarMatriculasDiscente(String discenteId) {
        return matriculaRepository.listarPorDiscente(discenteId);
    }
    
    /**
     * Lista todas as reservas de um discente.
     */
    public List<ReservaLivro> listarReservasDiscente(String discenteId) {
        return reservaRepository.listarPorDiscente(discenteId);
    }
    
    /**
     * Busca uma matrícula pelo código único.
     */
    public Matricula buscarMatriculaPorCodigo(String codigoMatricula) {
        return matriculaRepository.buscarPorCodigo(codigoMatricula);
    }
    
    /**
     * Simula reserva de livro usando código de matrícula.
     */
    public boolean simularReservaLivroPorCodigo(String codigoMatricula, String livroId) {
        // Buscar matrícula pelo código
        Matricula matricula = matriculaRepository.buscarPorCodigo(codigoMatricula);
        if (matricula == null) {
            Logger.erro("Código de matrícula não encontrado.");
            return false;
        }
        
        // Usar o discenteId da matrícula para fazer a reserva
        return simularReservaLivro(matricula.getDiscenteId(), livroId);
    }
    
    /**
     * Cancela reserva de livro usando código de matrícula.
     */
    public boolean cancelarReservaLivroPorCodigo(String codigoMatricula, String livroId) {
        // Buscar matrícula pelo código
        Matricula matricula = matriculaRepository.buscarPorCodigo(codigoMatricula);
        if (matricula == null) {
            Logger.erro("Código de matrícula não encontrado.");
            return false;
        }
        
        // Usar o discenteId da matrícula para cancelar a reserva
        return cancelarReservaLivro(matricula.getDiscenteId(), livroId);
    }
}
