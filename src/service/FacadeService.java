package service;

import model.Discente;
import model.Disciplina;
import model.Livro;
import util.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FacadeService {

    private final DiscenteService discenteService;
    private final DisciplinaService disciplinaService;
    private final BibliotecaService bibliotecaService;

    public FacadeService(
            DiscenteService discenteService,
            DisciplinaService disciplinaService,
            BibliotecaService bibliotecaService) {

        this.discenteService = discenteService;
        this.disciplinaService = disciplinaService;
        this.bibliotecaService = bibliotecaService;
    }

    public void inicializarCaches() {
        Logger.sistema("\nCarregando dados dos microsserviços em paralelo...");

        long startTime = System.currentTimeMillis();

        try {
            CompletableFuture<Void> discentesFuture = CompletableFuture.runAsync(() -> {
                discenteService.inicializarCache();
            });

            CompletableFuture<Void> disciplinasFuture = CompletableFuture.runAsync(() -> {
                disciplinaService.inicializarCache();
            });

            CompletableFuture<Void> livrosFuture = CompletableFuture.runAsync(() -> {
                bibliotecaService.inicializarCache();
            });

            CompletableFuture.allOf(discentesFuture, disciplinasFuture, livrosFuture).get();

        } catch (InterruptedException | ExecutionException e) {
            Logger.erro("Falha ao inicializar caches em paralelo: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        long duration = System.currentTimeMillis() - startTime;
        Logger.sistema("Caches inicializados em " + String.format("%.2f", duration / 1000.0) + "s");
        Logger.sistema("Dados carregados e prontos para uso!\n");
    }

    public Discente buscarDiscente(Long id) {
        return discenteService.buscarPorId(id);
    }

    public List<Discente> listarDiscentes() {
        return discenteService.listarTodos();
    }

    public Disciplina buscarDisciplina(Long id) {
        return disciplinaService.buscarPorId(id);
    }

    public List<Disciplina> listarDisciplinas() {
        return disciplinaService.listarTodas();
    }

    public List<Disciplina> listarDisciplinasPorCurso(String curso) {
        return disciplinaService.filtrarPorCurso(curso);
    }

    public Livro buscarLivro(Long id) {
        return bibliotecaService.buscarPorId(id);
    }

    public List<Livro> listarLivros() {
        return bibliotecaService.listarTodos();
    }
}
