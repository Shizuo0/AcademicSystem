package repository;

import model.Matricula;
import java.util.List;

public interface IMatriculaRepository {

    boolean adicionar(Matricula matricula);

    boolean remover(String discenteId, String disciplinaId);

    boolean removerPorCodigo(String codigoMatricula);

    List<Matricula> listarPorDiscente(String discenteId);

    int contarMatriculasPorDiscente(String discenteId);

    int contarMatriculasPorDisciplina(String disciplinaId);

    boolean existeMatricula(String discenteId, String disciplinaId);

    Matricula buscarPorCodigo(String codigoMatricula);
}
