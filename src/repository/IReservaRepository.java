package repository;

import model.ReservaLivro;
import java.util.List;

public interface IReservaRepository {
    
    /**
     * Adiciona uma nova reserva ao banco de dados.
     */
    boolean adicionar(ReservaLivro reserva);
    
    /**
     * Remove uma reserva do banco de dados.
     */
    boolean remover(String discenteId, String livroId);
    
    /**
     * Lista todas as reservas de um discente específico.
     */
    List<ReservaLivro> listarPorDiscente(String discenteId);
    
    /**
     * Verifica se um livro específico está reservado por algum discente.
     */
    boolean livroEstaReservado(String livroId);
    
    /**
     * Verifica se existe uma reserva específica.
     */
    boolean existeReserva(String discenteId, String livroId);
}
