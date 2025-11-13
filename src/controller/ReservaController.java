package controller;

import model.Livro;
import model.Matricula;
import model.ReservaLivro;
import service.FacadeService;
import service.GestaoAcademicaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsável pelas operações de Reserva de Livros.
 * Todas as operações de reserva são realizadas usando código de matrícula.
 */
public class ReservaController {
    
    private final GestaoAcademicaService gestaoService;
    private final FacadeService facadeService;
    
    public ReservaController(GestaoAcademicaService gestaoService, FacadeService facadeService) {
        this.gestaoService = gestaoService;
        this.facadeService = facadeService;
    }
    
    /**
     * Realiza uma reserva de livro usando código de matrícula.
     */
    public boolean realizarReserva(String codigoMatricula, String livroId) {
        try {
            return gestaoService.simularReservaLivro(codigoMatricula, livroId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Cancela uma reserva de livro usando código de matrícula.
     */
    public boolean cancelarReserva(String codigoMatricula, String livroId) {
        try {
            return gestaoService.cancelarReservaLivro(codigoMatricula, livroId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Consulta reservas usando código de matrícula.
     */
    public List<Map<String, Object>> consultarReservas(String codigoMatricula) {
        try {
            // Buscar matrícula pelo código
            Matricula matricula = gestaoService.buscarMatriculaPorCodigo(codigoMatricula);
            if (matricula == null) {
                return Collections.emptyList();
            }
            
            // Buscar reservas do discente
            String discenteId = matricula.getDiscenteId();
            List<ReservaLivro> reservas = gestaoService.listarReservasDiscente(discenteId);
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            for (ReservaLivro reserva : reservas) {
                Map<String, Object> info = new HashMap<>();
                try {
                    Long livroId = Long.parseLong(reserva.getLivroId());
                    Livro livro = facadeService.buscarLivro(livroId);
                    
                    if (livro != null) {
                        info.put("livroId", reserva.getLivroId());
                        info.put("titulo", livro.getTitulo());
                        info.put("autor", livro.getAutor());
                        info.put("dataReserva", reserva.getDataReserva());
                    } else {
                        info.put("livroId", reserva.getLivroId());
                        info.put("titulo", "Livro não encontrado");
                        info.put("autor", "N/A");
                        info.put("dataReserva", reserva.getDataReserva());
                    }
                } catch (Exception e) {
                    info.put("livroId", reserva.getLivroId());
                    info.put("titulo", "Erro ao buscar dados");
                    info.put("autor", "N/A");
                    info.put("dataReserva", reserva.getDataReserva());
                }
                
                resultado.add(info);
            }
            
            return resultado;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
