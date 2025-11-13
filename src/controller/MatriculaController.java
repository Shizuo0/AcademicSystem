package controller;

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
     * Cancela uma matrícula específica.
     */
    public boolean cancelarMatricula(String discenteId, String disciplinaId) {
        try {
            return gestaoService.cancelarMatricula(discenteId, disciplinaId);
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
     * Consulta todas as matrículas de um discente.
     */
    public List<Map<String, Object>> consultarMatriculas(String discenteId) {
        try {
            List<Matricula> matriculas = gestaoService.listarMatriculasDiscente(discenteId);
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            for (Matricula matricula : matriculas) {
                Map<String, Object> info = new HashMap<>();
                try {
                    Long disciplinaId = Long.parseLong(matricula.getDisciplinaId());
                    Disciplina disciplina = facadeService.buscarDisciplina(disciplinaId);
                    
                    if (disciplina != null) {
                        info.put("codigoMatricula", matricula.getCodigoMatricula());
                        info.put("disciplinaId", matricula.getDisciplinaId());
                        info.put("disciplinaNome", disciplina.getNome());
                        info.put("curso", disciplina.getCurso());
                        info.put("dataMatricula", matricula.getDataMatricula());
                    } else {
                        info.put("codigoMatricula", matricula.getCodigoMatricula());
                        info.put("disciplinaId", matricula.getDisciplinaId());
                        info.put("disciplinaNome", "Disciplina não encontrada");
                        info.put("curso", "N/A");
                        info.put("dataMatricula", matricula.getDataMatricula());
                    }
                } catch (Exception e) {
                    info.put("codigoMatricula", matricula.getCodigoMatricula());
                    info.put("disciplinaId", matricula.getDisciplinaId());
                    info.put("disciplinaNome", "Erro ao buscar dados");
                    info.put("curso", "N/A");
                    info.put("dataMatricula", matricula.getDataMatricula());
                }
                
                resultado.add(info);
            }
            
            return resultado;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
