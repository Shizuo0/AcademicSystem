package controller;

import model.Disciplina;
import service.DisponibilidadeService;
import service.FacadeService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsável pelas operações relacionadas a Disciplinas e Cursos.
 */
public class DisciplinaController {
    
    private final FacadeService facadeService;
    private final DisponibilidadeService disponibilidadeService;
    
    public DisciplinaController(FacadeService facadeService, DisponibilidadeService disponibilidadeService) {
        this.facadeService = facadeService;
        this.disponibilidadeService = disponibilidadeService;
    }
    
    /**
     * Lista todos os cursos disponíveis (únicos).
     */
    public List<String> listarCursosDisponiveis() {
        try {
            List<Disciplina> disciplinas = facadeService.listarDisciplinas();
            return disciplinas.stream()
                    .map(Disciplina::getCurso)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    /**
     * Lista disciplinas por curso, com vagas atualizadas.
     */
    public List<Disciplina> listarDisciplinasPorCurso(String curso) {
        try {
            List<Disciplina> disciplinas;
            
            if (curso == null || curso.trim().isEmpty()) {
                disciplinas = facadeService.listarDisciplinas();
            } else {
                disciplinas = facadeService.listarDisciplinasPorCurso(curso);
            }
            
            // Atualiza vagas disponíveis baseado nas matrículas
            for (Disciplina disciplina : disciplinas) {
                int vagasReais = disponibilidadeService.calcularVagasDisponiveis(
                    String.valueOf(disciplina.getId())
                );
                disciplina.setVagas(vagasReais);
            }
            
            return disciplinas;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
