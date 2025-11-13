package controller;

import model.Discente;
import service.FacadeService;

import java.util.Collections;
import java.util.List;

public class DiscenteController {

    private final FacadeService facadeService;

    public DiscenteController(FacadeService facadeService) {
        this.facadeService = facadeService;
    }

    public Discente consultarDiscente(String id) {
        try {
            Long idLong = Long.parseLong(id);
            return facadeService.buscarDiscente(idLong);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Discente> listarTodosDiscentes() {
        try {
            return facadeService.listarDiscentes();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
