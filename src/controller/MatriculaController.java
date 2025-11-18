package controller;

import model.Discente;
import model.Disciplina;
import model.Matricula;
import service.FacadeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatriculaController {

    private final FacadeService facadeService;

    public MatriculaController(FacadeService facadeService) {
        this.facadeService = facadeService;
    }

    public boolean realizarMatricula(String discenteId, String disciplinaId) {
        try {
            return facadeService.simularMatricula(discenteId, disciplinaId);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean cancelarMatriculaPorCodigo(String codigoMatricula) {
        try {
            return facadeService.cancelarMatriculaPorCodigo(codigoMatricula);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Map<String, Object>> consultarMatriculas(String discenteId) {
        try {
            List<Matricula> matriculas = facadeService.listarMatriculasDiscente(discenteId);
            List<Map<String, Object>> resultado = new ArrayList<>();

            Discente discente = null;
            try {
                Long idLong = Long.parseLong(discenteId);
                discente = facadeService.buscarDiscente(idLong);
            } catch (Exception e) {
            }

            for (Matricula matricula : matriculas) {
                Map<String, Object> info = new HashMap<>();

                info.put("codigoMatricula", matricula.getCodigoMatricula());
                info.put("discenteId", matricula.getDiscenteId());
                info.put("dataMatricula", matricula.getDataMatricula());

                if (discente != null) {
                    info.put("discenteNome", discente.getNome());
                    info.put("discenteCurso", discente.getCurso());
                } else {
                    info.put("discenteNome", "Não encontrado");
                    info.put("discenteCurso", "N/A");
                }

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
