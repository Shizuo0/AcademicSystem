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
    
    public DiscenteService(IHttpClient httpClient) {
        this.httpClient = httpClient;
        this.jsonParser = new GsonParser();
        this.cache = new HashMap<>();
    }
    
    public Discente buscarPorId(Long id) {
        if (cache.containsKey(id)) {
            Logger.debug("[CACHE HIT] Discente ID: " + id);
            return cache.get(id);
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            String url = DISCENTE_BASE_URL + "?id=" + id;
            String jsonResponse = null;
            
            try {
                jsonResponse = httpClient.get(url);
            } catch (IOException e) {
                url = DISCENTE_BASE_URL + "/" + id;
                jsonResponse = httpClient.get(url);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            Logger.debug("[TEMPO] Requisição discente ID " + id + ": " + 
                String.format("%.2f", duration / 1000.0) + "s");
            
            Discente discente;
            if (jsonResponse.trim().startsWith("[")) {
                java.util.List<Discente> lista = jsonParser.parseList(jsonResponse, Discente.class);
                discente = lista.isEmpty() ? null : lista.get(0);
            } else {
                discente = jsonParser.parseObject(jsonResponse, Discente.class);
            }
            
            if (discente != null) {
                cache.put(id, discente);
            }
            
            return discente;
            
        } catch (IOException e) {
            Logger.erro("Falha ao buscar discente ID " + id + ": " + e.getMessage());
            return null;
        }
    }
    
    public void limparCache() {
        cache.clear();
        Logger.debug("[CACHE] Cache de discentes limpo");
    }
    
    public java.util.List<Discente> listarTodos() {
        try {
            long startTime = System.currentTimeMillis();
            
            String jsonResponse = httpClient.get(DISCENTE_BASE_URL);
            
            long duration = System.currentTimeMillis() - startTime;
            Logger.debug("[TEMPO] Requisição listar discentes: " + 
                String.format("%.2f", duration / 1000.0) + "s");
            
            java.util.List<Discente> discentes = jsonParser.parseList(jsonResponse, Discente.class);
            
            for (Discente discente : discentes) {
                if (discente != null) {
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
