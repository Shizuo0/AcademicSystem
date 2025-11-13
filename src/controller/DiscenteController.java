package controller;

import model.Discente;
import service.FacadeService;

import java.util.Collections;
import java.util.List;

/**
 * Controller responsável pelas operações relacionadas a Discentes.
 */
public class DiscenteController {
    
    private final FacadeService facadeService;
    
    public DiscenteController(FacadeService facadeService) {
        this.facadeService = facadeService;
    }
    
    /**
     * Consulta um discente pelo ID.
     */
    public Discente consultarDiscente(String id) {
        try {
            Long idLong = Long.parseLong(id);
            return facadeService.buscarDiscente(idLong);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Lista todos os discentes disponíveis.
     */
    public List<Discente> listarTodosDiscentes() {
        try {
            return facadeService.listarDiscentes();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
