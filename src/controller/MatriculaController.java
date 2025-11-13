package controller;

import model.Discente;
import model.Disciplina;
import model.Matricula;
import service.FacadeService;
import service.GestaoAcademicaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsável pelas operações de Matrícula.
 */
public class MatriculaController {
    
    private final GestaoAcademicaService gestaoService;
    private final FacadeService facadeService;
    
    public MatriculaController(GestaoAcademicaService gestaoService, FacadeService facadeService) {
        this.gestaoService = gestaoService;
        this.facadeService = facadeService;
    }
    
    /**
     * Realiza uma matrícula de um discente em uma disciplina.
     */
    public boolean realizarMatricula(String discenteId, String disciplinaId) {
        try {
            return gestaoService.simularMatricula(discenteId, disciplinaId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Cancela uma matrícula pelo código.
     */
    public boolean cancelarMatriculaPorCodigo(String codigoMatricula) {
        try {
            return gestaoService.cancelarMatriculaPorCodigo(codigoMatricula);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Consulta todas as matrículas de um discente com informações enriquecidas.
     * Busca dados do discente e da disciplina para exibição completa.
     */
    public List<Map<String, Object>> consultarMatriculas(String discenteId) {
        try {
            List<Matricula> matriculas = gestaoService.listarMatriculasDiscente(discenteId);
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            // Buscar informações do discente uma única vez (otimização)
            Discente discente = null;
            try {
                Long idLong = Long.parseLong(discenteId);
                discente = facadeService.buscarDiscente(idLong);
            } catch (Exception e) {
                // Se falhar ao buscar discente, continua sem os dados
            }
            
            // Processar cada matrícula
            for (Matricula matricula : matriculas) {
                Map<String, Object> info = new HashMap<>();
                
                // Dados da matrícula (sempre disponíveis)
                info.put("codigoMatricula", matricula.getCodigoMatricula());
                info.put("discenteId", matricula.getDiscenteId());
                info.put("dataMatricula", matricula.getDataMatricula());
                
                // Dados do discente (se disponível)
                if (discente != null) {
                    info.put("discenteNome", discente.getNome());
                    info.put("discenteCurso", discente.getCurso());
                } else {
                    info.put("discenteNome", "Não encontrado");
                    info.put("discenteCurso", "N/A");
                }
                
                // Dados da disciplina (buscar do microsserviço)
                try {
                    Long disciplinaId = Long.parseLong(matricula.getDisciplinaId());
                    Disciplina disciplina = facadeService.buscarDisciplina(disciplinaId);
                    
                    if (disciplina != null) {
                        info.put("disciplinaId", matricula.getDisciplinaId());
                        info.put("disciplinaNome", disciplina.getNome());
                        info.put("disciplinaCurso", disciplina.getCurso());
                    } else {
                        info.put("disciplinaId", matricula.getDisciplinaId());
                        info.put("disciplinaNome", "Disciplina não encontrada");
                        info.put("disciplinaCurso", "N/A");
                    }
                } catch (Exception e) {
                    info.put("disciplinaId", matricula.getDisciplinaId());
                    info.put("disciplinaNome", "Erro ao buscar dados");
                    info.put("disciplinaCurso", "N/A");
                }
                
                resultado.add(info);
            }
            
            return resultado;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
