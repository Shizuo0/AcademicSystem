package controller;

import model.Disciplina;
import service.FacadeService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DisciplinaController {

    private final FacadeService facadeService;

    public DisciplinaController(FacadeService facadeService) {
        this.facadeService = facadeService;
    }

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

    public List<Disciplina> listarDisciplinasPorCurso(String curso) {
        try {
            List<Disciplina> disciplinas;

            if (curso == null || curso.trim().isEmpty()) {
                disciplinas = facadeService.listarDisciplinas();
            } else {
                disciplinas = facadeService.listarDisciplinasPorCurso(curso);
            }

            for (Disciplina disciplina : disciplinas) {
                int vagasReais = facadeService.calcularVagasDisponiveis(
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
