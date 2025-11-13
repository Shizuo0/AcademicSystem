package controller;

import model.Livro;
import model.StatusDisponibilidade;
import service.DisponibilidadeService;
import service.FacadeService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsável pelas operações relacionadas a Livros.
 */
public class BibliotecaController {
    
    private final FacadeService facadeService;
    private final DisponibilidadeService disponibilidadeService;
    
    public BibliotecaController(FacadeService facadeService, DisponibilidadeService disponibilidadeService) {
        this.facadeService = facadeService;
        this.disponibilidadeService = disponibilidadeService;
    }
    
    /**
     * Lista livros disponíveis para reserva.
     */
    public List<Livro> listarLivrosDisponiveis() {
        try {
            List<Livro> todosLivros = facadeService.listarLivros();
            return todosLivros.stream()
                    .filter(livro -> {
                        if (livro.getStatusDisponibilidade() != StatusDisponibilidade.DISPONIVEL) {
                            return false;
                        }
                        return disponibilidadeService.verificarLivroDisponivel(
                            String.valueOf(livro.getId())
                        );
                    })
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
