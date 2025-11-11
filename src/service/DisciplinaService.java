package service;

import model.Disciplina;
import util.IHttpClient;
import util.GsonParser;
import util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DisciplinaService {
    
    private static final String DISCIPLINA_BASE_URL = "https://sswfuybfs8.execute-api.us-east-2.amazonaws.com/disciplinaServico/msDisciplina";
    
    private final IHttpClient httpClient;
    private final GsonParser jsonParser;
    private final Map<String, List<Disciplina>> cacheListaPorCurso;
    private List<Disciplina> cacheListaCompleta;
    private long ultimaAtualizacao;
    private static final long CACHE_EXPIRATION_MS = 300000;

    public DisciplinaService(IHttpClient httpClient) {
        this.httpClient = httpClient;
        this.jsonParser = new GsonParser();
        this.cacheListaPorCurso = new HashMap<>();
        this.cacheListaCompleta = null;
        this.ultimaAtualizacao = 0;
    }

    public List<Disciplina> listarTodas() {

        if (cacheListaCompleta != null && !isCacheExpirado()) {
            Logger.debug("[CACHE HIT] Lista completa de disciplinas");
            return new ArrayList<>(cacheListaCompleta);
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            String jsonResponse = httpClient.get(DISCIPLINA_BASE_URL);
            
            long duration = System.currentTimeMillis() - startTime;
            Logger.debug("[TEMPO] Requisição listar disciplinas: " + 
                String.format("%.2f", duration / 1000.0) + "s");

            List<Disciplina> disciplinas = jsonParser.parseList(jsonResponse, Disciplina.class);

            cacheListaCompleta = new ArrayList<>(disciplinas);
            cacheListaPorCurso.clear();
            ultimaAtualizacao = System.currentTimeMillis();
            
            return disciplinas;
            
        } catch (IOException e) {
            Logger.erro("Falha ao listar disciplinas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Disciplina> filtrarPorCurso(String curso) {
        if (curso == null || curso.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String cursoNormalizado = curso.trim().toLowerCase();

        if (cacheListaPorCurso.containsKey(cursoNormalizado) && !isCacheExpirado()) {
            Logger.debug("[CACHE HIT] Disciplinas do curso: " + curso);
            return new ArrayList<>(cacheListaPorCurso.get(cursoNormalizado));
        }

        List<Disciplina> todasDisciplinas = listarTodas();
        List<Disciplina> disciplinasDoCurso = todasDisciplinas.stream()
            .filter(d -> d.getCurso() != null && 
                        d.getCurso().toLowerCase().contains(cursoNormalizado))
            .collect(Collectors.toList());

        cacheListaPorCurso.put(cursoNormalizado, new ArrayList<>(disciplinasDoCurso));
        
        return disciplinasDoCurso;
    }

    public Disciplina buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        
        List<Disciplina> todasDisciplinas = listarTodas();
        return todasDisciplinas.stream()
            .filter(d -> d.getId() != null && d.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<Disciplina> buscarComVagas() {
        List<Disciplina> todasDisciplinas = listarTodas();
        return todasDisciplinas.stream()
            .filter(d -> d.getVagas() != null && d.getVagas() > 0)
            .collect(Collectors.toList());
    }

    public void limparCache() {
        cacheListaCompleta = null;
        cacheListaPorCurso.clear();
        ultimaAtualizacao = 0;
        Logger.debug("[CACHE] Cache de disciplinas limpo");
    }

    private boolean isCacheExpirado() {
        return (System.currentTimeMillis() - ultimaAtualizacao) > CACHE_EXPIRATION_MS;
    }
}
