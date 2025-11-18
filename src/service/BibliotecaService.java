package service;

import model.Livro;
import model.StatusDisponibilidade;
import util.IHttpClient;
import util.GsonParser;
import util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BibliotecaService {

    private static final String BIBLIOTECA_BASE_URL = "https://qiiw8bgxka.execute-api.us-east-2.amazonaws.com/acervo/biblioteca";

    private final IHttpClient httpClient;
    private final GsonParser jsonParser;
    private List<Livro> cacheLivros;
    private boolean cacheInicializado;

    public BibliotecaService(IHttpClient httpClient, GsonParser jsonParser) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.cacheLivros = null;
        this.cacheInicializado = false;
    }

    public void inicializarCache() {
        if (cacheInicializado) {
            Logger.debug("[CACHE] Livros já inicializados");
            return;
        }

        Logger.info("[CACHE] Inicializando cache de livros...");
        listarTodos();
        cacheInicializado = true;
        Logger.info("[CACHE] Cache de livros inicializado com " +
            (cacheLivros != null ? cacheLivros.size() : 0) + " registros");
    }

    public List<Livro> listarTodos() {
        if (cacheLivros != null) {
            Logger.debug("[CACHE HIT] Lista de livros");
            return new ArrayList<>(cacheLivros);
        }

        try {
            long startTime = System.currentTimeMillis();

            String jsonResponse = httpClient.get(BIBLIOTECA_BASE_URL);

            long duration = System.currentTimeMillis() - startTime;
            double durationSeconds = duration / 1000.0;

            if (durationSeconds > 3.0) {
                Logger.aviso("Requisição listar livros demorou " +
                    String.format("%.2f", durationSeconds) + "s (limite: 3.0s)");
            }

            List<Livro> livros = jsonParser.parseList(jsonResponse, Livro.class);

            cacheLivros = new ArrayList<>(livros);

            return livros;

        } catch (IOException e) {
            Logger.erro("Falha ao listar livros: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Livro> filtrarPorDisponibilidade(StatusDisponibilidade statusDisponibilidade) {
        if (statusDisponibilidade == null) {
            return new ArrayList<>();
        }

        List<Livro> todosLivros = listarTodos();
        return todosLivros.stream()
            .filter(livro -> livro.getStatusDisponibilidade() == statusDisponibilidade)
            .collect(Collectors.toList());
    }

    public Livro buscarPorId(Long id) {
        if (id == null) {
            return null;
        }

        List<Livro> todosLivros = listarTodos();
        return todosLivros.stream()
            .filter(livro -> livro.getId() != null && livro.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<Livro> listarDisponiveis() {
        return filtrarPorDisponibilidade(StatusDisponibilidade.DISPONIVEL);
    }
}
