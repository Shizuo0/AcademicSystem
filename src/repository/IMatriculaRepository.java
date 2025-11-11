package repository;

import model.Matricula;
import java.util.List;

public interface IMatriculaRepository {
    
    /**
     * Adiciona uma nova matrícula ao banco de dados.
     */
    boolean adicionar(Matricula matricula);
    
    /**
     * Remove uma matrícula do banco de dados.
     */
    boolean remover(String discenteId, String disciplinaId);
    
    /**
     * Remove uma matrícula pelo código único.
     */
    boolean removerPorCodigo(String codigoMatricula);
    
    /**
     * Lista todas as matrículas de um discente específico.
     */
    List<Matricula> listarPorDiscente(String discenteId);
    
    /**
     * Conta o número de matrículas de um discente específico.
     */
    int contarMatriculasPorDiscente(String discenteId);
    
    /**
     * Conta o número de alunos matriculados em uma disciplina específica.
     */
    int contarMatriculasPorDisciplina(String disciplinaId);
    
    /**
     * Verifica se existe uma matrícula específica.
     */
    boolean existeMatricula(String discenteId, String disciplinaId);
    
    /**
     * Busca uma matrícula pelo código único.
     */
    Matricula buscarPorCodigo(String codigoMatricula);
}
