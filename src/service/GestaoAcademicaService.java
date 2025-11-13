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

    public boolean simularMatricula(String discenteId, String disciplinaId) {
        try {
            disponibilidadeService.validarMatricula(discenteId, disciplinaId);

            if (matriculaRepository.existeMatricula(discenteId, disciplinaId)) {
                Logger.erro("Discente já matriculado nesta disciplina.");
                return false;
            }

            String codigoMatricula = gerarCodigoMatriculaUnico();

            Matricula matricula = Matricula.nova(codigoMatricula, discenteId, disciplinaId, LocalDate.now());

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

    private String gerarCodigoMatriculaUnico() {
        long timestamp = System.currentTimeMillis();
        int ordem = (int) (timestamp % 9999) + 1;
        return GeradorMatricula.gerarComDataAtual(ordem);
    }

    public boolean cancelarMatriculaPorCodigo(String codigoMatricula) {
        boolean sucesso = matriculaRepository.removerPorCodigo(codigoMatricula);

        if (sucesso) {
            Logger.sucesso("Matrícula cancelada: " + codigoMatricula);
            return true;
        } else {
            Logger.erro("Matrícula não encontrada.");
            return false;
        }
    }

    public List<Matricula> listarMatriculasDiscente(String discenteId) {
        return matriculaRepository.listarPorDiscente(discenteId);
    }

    public List<ReservaLivro> listarReservasDiscente(String discenteId) {
        return reservaRepository.listarPorDiscente(discenteId);
    }

    public Matricula buscarMatriculaPorCodigo(String codigoMatricula) {
        return matriculaRepository.buscarPorCodigo(codigoMatricula);
    }

    public boolean simularReservaLivro(String codigoMatricula, String livroId) {
        Matricula matricula = matriculaRepository.buscarPorCodigo(codigoMatricula);
        if (matricula == null) {
            Logger.erro("Código de matrícula não encontrado.");
            return false;
        }

        String discenteId = matricula.getDiscenteId();

        try {
            disponibilidadeService.validarReservaLivro(livroId);

            if (reservaRepository.existeReserva(discenteId, livroId)) {
                Logger.erro("Você já reservou este livro.");
                return false;
            }

            ReservaLivro reserva = ReservaLivro.nova(discenteId, livroId, LocalDate.now());

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

    public boolean cancelarReservaLivro(String codigoMatricula, String livroId) {
        Matricula matricula = matriculaRepository.buscarPorCodigo(codigoMatricula);
        if (matricula == null) {
            Logger.erro("Código de matrícula não encontrado.");
            return false;
        }

        String discenteId = matricula.getDiscenteId();

        if (!reservaRepository.existeReserva(discenteId, livroId)) {
            Logger.erro("Reserva não encontrada.");
            return false;
        }

        boolean sucesso = reservaRepository.remover(discenteId, livroId);

        if (sucesso) {
            Logger.sucesso("Reserva cancelada.");
            return true;
        }

        return false;
    }
}
