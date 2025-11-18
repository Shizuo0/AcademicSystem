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
    private boolean cacheInicializado;

    public DisciplinaService(IHttpClient httpClient, GsonParser jsonParser) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.cacheListaPorCurso = new HashMap<>();
        this.cacheListaCompleta = null;
        this.cacheInicializado = false;
    }

    public void inicializarCache() {
        if (cacheInicializado) {
            Logger.debug("[CACHE] Disciplinas já inicializadas");
            return;
        }

        Logger.info("[CACHE] Inicializando cache de disciplinas...");
        listarTodas();
        cacheInicializado = true;
        Logger.info("[CACHE] Cache de disciplinas inicializado com " +
            (cacheListaCompleta != null ? cacheListaCompleta.size() : 0) + " registros");
    }

    public List<Disciplina> listarTodas() {
        if (cacheListaCompleta != null) {
            Logger.debug("[CACHE HIT] Lista completa de disciplinas");
            return new ArrayList<>(cacheListaCompleta);
        }

        try {
            long startTime = System.currentTimeMillis();

            String jsonResponse = httpClient.get(DISCIPLINA_BASE_URL);

            long duration = System.currentTimeMillis() - startTime;
            double durationSeconds = duration / 1000.0;

            if (durationSeconds > 3.0) {
                Logger.aviso("Requisição listar disciplinas demorou " +
                    String.format("%.2f", durationSeconds) + "s (limite: 3.0s)");
            }

            List<Disciplina> disciplinas = jsonParser.parseList(jsonResponse, Disciplina.class);

            cacheListaCompleta = new ArrayList<>(disciplinas);
            cacheListaPorCurso.clear();

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

        if (cacheListaPorCurso.containsKey(cursoNormalizado)) {
            Logger.debug("[CACHE HIT] Disciplinas do curso: " + curso);
            return new ArrayList<>(cacheListaPorCurso.get(cursoNormalizado));
        }

        if (!cacheInicializado) {
            inicializarCache();
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
}
