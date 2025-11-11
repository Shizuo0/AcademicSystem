package service;

import exception.CursoIncompativelException;
import exception.DiscenteInativoException;
import exception.LimiteMatriculasExcedidoException;
import exception.LivroIndisponivelException;
import exception.SemVagasException;
import model.Discente;
import model.Disciplina;
import model.Livro;
import model.SituacaoAcademica;
import model.StatusDisponibilidade;
import repository.IMatriculaRepository;
import repository.IReservaRepository;
import util.Logger;

public class DisponibilidadeService {
    
    private final DiscenteService discenteService;
    private final DisciplinaService disciplinaService;
    private final BibliotecaService bibliotecaService;
    private final IMatriculaRepository matriculaRepository;
    private final IReservaRepository reservaRepository;
    
    // Constantes de validação
    private static final int LIMITE_MAXIMO_DISCIPLINAS = 5;
    
    public DisponibilidadeService(
            DiscenteService discenteService,
            DisciplinaService disciplinaService,
            BibliotecaService bibliotecaService,
            IMatriculaRepository matriculaRepository,
            IReservaRepository reservaRepository) {
        this.discenteService = discenteService;
        this.disciplinaService = disciplinaService;
        this.bibliotecaService = bibliotecaService;
        this.matriculaRepository = matriculaRepository;
        this.reservaRepository = reservaRepository;
    }
    
    /**
     * Calcula o número de vagas disponíveis em uma disciplina.
     */
    public int calcularVagasDisponiveis(String disciplinaId) {
        if (disciplinaId == null || disciplinaId.trim().isEmpty()) {
            return 0;
        }
        
        try {
            // 1. Buscar disciplina no microsserviço
            Long id = Long.parseLong(disciplinaId);
            Disciplina disciplina = disciplinaService.buscarPorId(id);
            
            if (disciplina == null) {
                return 0;
            }
            
            // 2. Obter vagas originais do microsserviço
            Integer vagasOriginais = disciplina.getVagas();
            if (vagasOriginais == null || vagasOriginais <= 0) {
                return 0;
            }
            
            // 3. Contar matrículas no MySQL
            int matriculasNoBanco = matriculaRepository.contarMatriculasPorDisciplina(disciplinaId);
            
            // 4. Calcular disponibilidade real
            int vagasDisponiveis = vagasOriginais - matriculasNoBanco;
            
            // 5. Garantir que nunca retorna negativo
            vagasDisponiveis = Math.max(0, vagasDisponiveis);
            
            Logger.debug("Disciplina " + disciplinaId + ": " + vagasOriginais + 
                " vagas originais - " + matriculasNoBanco + " matrículas = " + 
                vagasDisponiveis + " vagas disponíveis");
            
            return vagasDisponiveis;
            
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Verifica se um livro está efetivamente disponível para reserva.
     */
    public boolean verificarLivroDisponivel(String livroId) {
        if (livroId == null || livroId.trim().isEmpty()) {
            return false;
        }
        
        try {
            // 1. Buscar livro no microsserviço
            Long id = Long.parseLong(livroId);
            Livro livro = bibliotecaService.buscarPorId(id);
            
            if (livro == null) {
                return false;
            }
            
            // 2. Verificar status no microsserviço
            boolean disponivelNoMicrosservico = 
                livro.getStatusDisponibilidade() == StatusDisponibilidade.DISPONIVEL;
            
            if (!disponivelNoMicrosservico) {
                Logger.debug("Livro " + livroId + " está INDISPONIVEL no microsserviço");
                return false;
            }
            
            // 3. Verificar se há reserva no MySQL
            boolean reservadoNoBanco = reservaRepository.livroEstaReservado(livroId);
            
            if (reservadoNoBanco) {
                Logger.debug("Livro " + livroId + " está DISPONIVEL no microsserviço mas JÁ RESERVADO no MySQL");
                return false;
            }
            
            // 4. Livro está disponível
            Logger.debug("Livro " + livroId + " está DISPONIVEL para reserva");
            return true;
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Verifica se um discente pode se matricular em uma disciplina.
     * Lança exceptions específicas para cada tipo de violação.
     */
    public void validarMatricula(String discenteId, String disciplinaId) 
            throws DiscenteInativoException, CursoIncompativelException, 
                   LimiteMatriculasExcedidoException, SemVagasException {
        
        // Validação básica de parâmetros
        if (discenteId == null || discenteId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do discente é inválido");
        }
        
        if (disciplinaId == null || disciplinaId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da disciplina é inválido");
        }
        
        try {
            // VALIDAÇÃO 1: Buscar discente no microsserviço
            Long idDiscente = Long.parseLong(discenteId);
            Discente discente = discenteService.buscarPorId(idDiscente);
            
            if (discente == null) {
                throw new IllegalArgumentException("Discente não encontrado: " + discenteId);
            }
            
            // VALIDAÇÃO 2: Buscar disciplina no microsserviço
            Long idDisciplina = Long.parseLong(disciplinaId);
            Disciplina disciplina = disciplinaService.buscarPorId(idDisciplina);
            
            if (disciplina == null) {
                throw new IllegalArgumentException("Disciplina não encontrada: " + disciplinaId);
            }
            
            // VALIDAÇÃO 3: Verificar situação acadêmica do discente
            if (discente.getSituacaoAcademica() != SituacaoAcademica.ATIVO) {
                throw new DiscenteInativoException(
                    "Discente com matrícula trancada. Situação: " + discente.getSituacaoAcademica()
                );
            }
            
            // VALIDAÇÃO 4: Verificar se discente já atingiu limite de disciplinas
            int totalMatriculasDiscente = matriculaRepository.contarMatriculasPorDiscente(discenteId);
            
            if (totalMatriculasDiscente >= LIMITE_MAXIMO_DISCIPLINAS) {
                throw new LimiteMatriculasExcedidoException(
                    "Limite de disciplinas atingido",
                    LIMITE_MAXIMO_DISCIPLINAS,
                    totalMatriculasDiscente
                );
            }
            
            // VALIDAÇÃO 5: Verificar se disciplina tem vagas disponíveis
            int vagasDisponiveis = calcularVagasDisponiveis(disciplinaId);
            
            if (vagasDisponiveis <= 0) {
                throw new SemVagasException(
                    "Disciplina sem vagas disponíveis",
                    disciplinaId,
                    vagasDisponiveis
                );
            }
            
            // VALIDAÇÃO 6: Verificar se disciplina pertence ao curso do discente
            String cursoDiscente = discente.getCurso();
            String cursoDisciplina = disciplina.getCurso();
            
            if (cursoDiscente == null || cursoDisciplina == null || 
                !cursoDiscente.trim().equalsIgnoreCase(cursoDisciplina.trim())) {
                throw new CursoIncompativelException(
                    "Disciplina não pertence ao curso do discente",
                    cursoDisciplina,
                    cursoDiscente
                );
            }
            
            // TODAS AS VALIDAÇÕES PASSARAM
            Logger.debug("Validação aprovada para matrícula: Discente " + discenteId + 
                ", Disciplina " + disciplinaId + ", Vagas: " + vagasDisponiveis);
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("IDs inválidos (devem ser numéricos)");
        }
    }
    
    /**
     * Valida se um livro está disponível para reserva.
     * Lança exception se não estiver disponível.
     */
    public void validarReservaLivro(String livroId) throws LivroIndisponivelException {
        if (livroId == null || livroId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do livro é inválido");
        }
        
        try {
            // 1. Buscar livro no microsserviço
            Long id = Long.parseLong(livroId);
            Livro livro = bibliotecaService.buscarPorId(id);
            
            if (livro == null) {
                throw new LivroIndisponivelException("Livro não encontrado", livroId);
            }
            
            // 2. Verificar status no microsserviço
            boolean disponivelNoMicrosservico = 
                livro.getStatusDisponibilidade() == StatusDisponibilidade.DISPONIVEL;
            
            if (!disponivelNoMicrosservico) {
                throw new LivroIndisponivelException(
                    "Livro está INDISPONÍVEL no microsserviço", 
                    livroId
                );
            }
            
            // 3. Verificar se há reserva no MySQL
            boolean reservadoNoBanco = reservaRepository.livroEstaReservado(livroId);
            
            if (reservadoNoBanco) {
                throw new LivroIndisponivelException(
                    "Livro já está reservado por outro discente", 
                    livroId
                );
            }
            
            // Livro está disponível
            Logger.debug("Livro disponível para reserva: " + livro.getTitulo());
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID do livro inválido (deve ser numérico)");
        }
    }
}
