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

    public int calcularVagasDisponiveis(String disciplinaId) {
        if (disciplinaId == null || disciplinaId.trim().isEmpty()) {
            return 0;
        }

        try {
            Long id = Long.parseLong(disciplinaId);
            Disciplina disciplina = disciplinaService.buscarPorId(id);

            if (disciplina == null) {
                return 0;
            }

            Integer vagasOriginais = disciplina.getVagas();
            if (vagasOriginais == null || vagasOriginais <= 0) {
                return 0;
            }

            int matriculasNoBanco = matriculaRepository.contarMatriculasPorDisciplina(disciplinaId);

            int vagasDisponiveis = vagasOriginais - matriculasNoBanco;

            vagasDisponiveis = Math.max(0, vagasDisponiveis);

            Logger.debug("Disciplina " + disciplinaId + ": " + vagasOriginais +
                " vagas originais - " + matriculasNoBanco + " matrículas = " +
                vagasDisponiveis + " vagas disponíveis");

            return vagasDisponiveis;

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean verificarLivroDisponivel(String livroId) {
        if (livroId == null || livroId.trim().isEmpty()) {
            return false;
        }

        try {
            Long id = Long.parseLong(livroId);
            Livro livro = bibliotecaService.buscarPorId(id);

            if (livro == null) {
                return false;
            }

            boolean disponivelNoMicrosservico =
                livro.getStatusDisponibilidade() == StatusDisponibilidade.DISPONIVEL;

            if (!disponivelNoMicrosservico) {
                Logger.debug("Livro " + livroId + " está INDISPONIVEL no microsserviço");
                return false;
            }

            boolean reservadoNoBanco = reservaRepository.livroEstaReservado(livroId);

            if (reservadoNoBanco) {
                Logger.debug("Livro " + livroId + " está DISPONIVEL no microsserviço mas JÁ RESERVADO no MySQL");
                return false;
            }

            Logger.debug("Livro " + livroId + " está DISPONIVEL para reserva");
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void validarMatricula(String discenteId, String disciplinaId)
            throws DiscenteInativoException, CursoIncompativelException,
                   LimiteMatriculasExcedidoException, SemVagasException {

        if (discenteId == null || discenteId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do discente é inválido");
        }

        if (disciplinaId == null || disciplinaId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da disciplina é inválido");
        }

        try {
            Long idDiscente = Long.parseLong(discenteId);
            Discente discente = discenteService.buscarPorId(idDiscente);

            if (discente == null) {
                throw new IllegalArgumentException("Discente não encontrado: " + discenteId);
            }

            Long idDisciplina = Long.parseLong(disciplinaId);
            Disciplina disciplina = disciplinaService.buscarPorId(idDisciplina);

            if (disciplina == null) {
                throw new IllegalArgumentException("Disciplina não encontrada: " + disciplinaId);
            }

            if (discente.getSituacaoAcademica() != SituacaoAcademica.ATIVO) {
                throw new DiscenteInativoException(
                    "Discente com matrícula trancada. Situação: " + discente.getSituacaoAcademica()
                );
            }

            int totalMatriculasDiscente = matriculaRepository.contarMatriculasPorDiscente(discenteId);

            if (totalMatriculasDiscente >= LIMITE_MAXIMO_DISCIPLINAS) {
                throw new LimiteMatriculasExcedidoException(
                    "Limite de disciplinas atingido",
                    LIMITE_MAXIMO_DISCIPLINAS,
                    totalMatriculasDiscente
                );
            }

            int vagasDisponiveis = calcularVagasDisponiveis(disciplinaId);

            if (vagasDisponiveis <= 0) {
                throw new SemVagasException(
                    "Disciplina sem vagas disponíveis",
                    disciplinaId,
                    vagasDisponiveis
                );
            }

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

            Logger.debug("Validação aprovada para matrícula: Discente " + discenteId +
                ", Disciplina " + disciplinaId + ", Vagas: " + vagasDisponiveis);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("IDs inválidos (devem ser numéricos)");
        }
    }

    public void validarReservaLivro(String livroId) throws LivroIndisponivelException {
        if (livroId == null || livroId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do livro é inválido");
        }

        try {
            Long id = Long.parseLong(livroId);
            Livro livro = bibliotecaService.buscarPorId(id);

            if (livro == null) {
                throw new LivroIndisponivelException("Livro não encontrado", livroId);
            }

            boolean disponivelNoMicrosservico =
                livro.getStatusDisponibilidade() == StatusDisponibilidade.DISPONIVEL;

            if (!disponivelNoMicrosservico) {
                throw new LivroIndisponivelException(
                    "Livro está INDISPONÍVEL no microsserviço",
                    livroId
                );
            }

            boolean reservadoNoBanco = reservaRepository.livroEstaReservado(livroId);

            if (reservadoNoBanco) {
                throw new LivroIndisponivelException(
                    "Livro já está reservado por outro discente",
                    livroId
                );
            }

            Logger.debug("Livro disponível para reserva: " + livro.getTitulo());

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID do livro inválido (deve ser numérico)");
        }
    }
}
