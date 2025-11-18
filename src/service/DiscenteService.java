package service;

import model.Discente;
import model.SituacaoAcademica;
import util.IHttpClient;
import util.GsonParser;
import util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiscenteService {

    private static final String DISCENTE_BASE_URL = "https://rmi6vdpsq8.execute-api.us-east-2.amazonaws.com/msAluno";

    private final IHttpClient httpClient;
    private final GsonParser jsonParser;
    private final Map<Long, Discente> cache;
    private java.util.List<Discente> cacheListaCompleta;
    private boolean cacheInicializado;

    public DiscenteService(IHttpClient httpClient, GsonParser jsonParser) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.cache = new HashMap<>();
        this.cacheListaCompleta = null;
        this.cacheInicializado = false;
    }

    public void inicializarCache() {
        if (cacheInicializado) {
            Logger.debug("[CACHE] Discentes já inicializados");
            return;
        }

        Logger.info("[CACHE] Inicializando cache de discentes...");
        listarTodos();
        cacheInicializado = true;
        Logger.info("[CACHE] Cache de discentes inicializado com " + cache.size() + " registros");
    }

    public Discente buscarPorId(Long id) {
        if (!cacheInicializado) {
            inicializarCache();
        }

        if (cache.containsKey(id)) {
            Logger.debug("[CACHE HIT] Discente ID: " + id);
            return cache.get(id);
        }

        Logger.debug("[CACHE MISS] Discente ID: " + id + " não encontrado no cache");
        return null;
    }

    public java.util.List<Discente> listarTodos() {
        if (cacheListaCompleta != null) {
            Logger.debug("[CACHE HIT] Lista completa de discentes");
            return new java.util.ArrayList<>(cacheListaCompleta);
        }

        try {
            long startTime = System.currentTimeMillis();

            String jsonResponse = httpClient.get(DISCENTE_BASE_URL);

            long duration = System.currentTimeMillis() - startTime;
            double durationSeconds = duration / 1000.0;

            if (durationSeconds > 3.0) {
                Logger.aviso("Requisição listar discentes demorou " +
                    String.format("%.2f", durationSeconds) + "s (limite: 3.0s)");
            }

            java.util.List<Discente> discentes = jsonParser.parseList(jsonResponse, Discente.class);

            cacheListaCompleta = new java.util.ArrayList<>(discentes);
            for (Discente discente : discentes) {
                if (discente != null && discente.getId() != null) {
                    cache.put(discente.getId(), discente);
                }
            }

            return discentes;

        } catch (IOException e) {
            Logger.erro("Falha ao listar discentes: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public boolean isDiscenteAtivo(Long id) {
        Discente discente = buscarPorId(id);
        return discente != null &&
               discente.getSituacaoAcademica() == SituacaoAcademica.ATIVO;
    }
}
