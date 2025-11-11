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
    private long ultimaAtualizacao;
    private static final long CACHE_EXPIRATION_MS = 300000;

    public BibliotecaService(IHttpClient httpClient) {
        this.httpClient = httpClient;
        this.jsonParser = new GsonParser();
        this.cacheLivros = null;
        this.ultimaAtualizacao = 0;
    }

    public List<Livro> listarTodos() {

        if (cacheLivros != null && !isCacheExpirado()) {
            Logger.debug("[CACHE HIT] Lista de livros");
            return new ArrayList<>(cacheLivros);
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            String jsonResponse = httpClient.get(BIBLIOTECA_BASE_URL);
            
            long duration = System.currentTimeMillis() - startTime;
            Logger.debug("[TEMPO] Requisição listar livros: " + 
                String.format("%.2f", duration / 1000.0) + "s");

            List<Livro> livros = jsonParser.parseList(jsonResponse, Livro.class);

            cacheLivros = new ArrayList<>(livros);
            ultimaAtualizacao = System.currentTimeMillis();
            
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
    
    public void limparCache() {
        cacheLivros = null;
        ultimaAtualizacao = 0;
        Logger.debug("[CACHE] Cache de livros limpo");
    }

    private boolean isCacheExpirado() {
        return (System.currentTimeMillis() - ultimaAtualizacao) > CACHE_EXPIRATION_MS;
    }
}
