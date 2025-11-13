package repository;

import model.ReservaLivro;
import java.util.List;

public interface IReservaRepository {

    boolean adicionar(ReservaLivro reserva);

    boolean remover(String discenteId, String livroId);

    List<ReservaLivro> listarPorDiscente(String discenteId);

    boolean livroEstaReservado(String livroId);

    boolean existeReserva(String discenteId, String livroId);
}
