package repository;

import model.Matricula;
import util.DatabaseConnection;
import util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatriculaRepositoryImpl implements IMatriculaRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SQL_INSERT =
        "INSERT INTO matriculas (codigo_matricula, discente_id, disciplina_id, data_matricula) VALUES (?, ?, ?, ?)";

    private static final String SQL_DELETE =
        "DELETE FROM matriculas WHERE discente_id = ? AND disciplina_id = ?";

    private static final String SQL_DELETE_BY_CODIGO =
        "DELETE FROM matriculas WHERE codigo_matricula = ?";

    private static final String SQL_SELECT_BY_DISCENTE =
        "SELECT id, codigo_matricula, discente_id, disciplina_id, data_matricula FROM matriculas WHERE discente_id = ?";

    private static final String SQL_COUNT_BY_DISCENTE =
        "SELECT COUNT(*) FROM matriculas WHERE discente_id = ?";

    private static final String SQL_COUNT_BY_DISCIPLINA =
        "SELECT COUNT(*) FROM matriculas WHERE disciplina_id = ?";

    private static final String SQL_EXISTS =
        "SELECT COUNT(*) FROM matriculas WHERE discente_id = ? AND disciplina_id = ?";

    private static final String SQL_SELECT_BY_CODIGO =
        "SELECT id, codigo_matricula, discente_id, disciplina_id, data_matricula FROM matriculas WHERE codigo_matricula = ?";

    public MatriculaRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public boolean adicionar(Matricula matricula) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, matricula.getCodigoMatricula());
            stmt.setString(2, matricula.getDiscenteId());
            stmt.setString(3, matricula.getDisciplinaId());
            stmt.setDate(4, Date.valueOf(matricula.getDataMatricula()));

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                Logger.debug("[MatriculaRepository] Matrícula duplicada");
            } else {
                Logger.erro("[MatriculaRepository] Erro SQL: " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean remover(String discenteId, String disciplinaId) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setString(1, discenteId);
            stmt.setString(2, disciplinaId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("[MatriculaRepository] Matrícula não encontrada");
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[MatriculaRepository] Erro ao remover: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removerPorCodigo(String codigoMatricula) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_CODIGO)) {

            stmt.setString(1, codigoMatricula);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                Logger.debug("[MatriculaRepository] Matrícula não encontrada");
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            Logger.erro("[MatriculaRepository] Erro ao remover: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Matricula> listarPorDiscente(String discenteId) {
        List<Matricula> matriculas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = databaseConnection.getConnection();
            if (conn == null) {
                Logger.erro("[MatriculaRepository] Falha ao obter conexão com o banco.");
                return matriculas;
            }

            stmt = conn.prepareStatement(SQL_SELECT_BY_DISCENTE);
            stmt.setString(1, discenteId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Matricula matricula = Matricula.doBanco(
                    rs.getInt("id"),
                    rs.getString("codigo_matricula"),
                    rs.getString("discente_id"),
                    rs.getString("disciplina_id"),
                    rs.getDate("data_matricula").toLocalDate()
                );
                matriculas.add(matricula);
            }

        } catch (SQLException e) {
            Logger.erro("[MatriculaRepository] Erro ao listar matrículas: " + e.getMessage());

        } finally {
            DatabaseConnection.fecharRecursos(rs, stmt, conn);
        }

        return matriculas;
    }

    @Override
    public int contarMatriculasPorDiscente(String discenteId) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_BY_DISCENTE)) {

            stmt.setString(1, discenteId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;

        } catch (SQLException e) {
            Logger.erro("[MatriculaRepository] Erro ao contar: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int contarMatriculasPorDisciplina(String disciplinaId) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_BY_DISCIPLINA)) {

            stmt.setString(1, disciplinaId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;

        } catch (SQLException e) {
            Logger.erro("[MatriculaRepository] Erro ao contar: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean existeMatricula(String discenteId, String disciplinaId) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS)) {

            stmt.setString(1, discenteId);
            stmt.setString(2, disciplinaId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            Logger.erro("[MatriculaRepository] Erro ao verificar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Matricula buscarPorCodigo(String codigoMatricula) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_CODIGO)) {

            stmt.setString(1, codigoMatricula);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Matricula.doBanco(
                        rs.getInt("id"),
                        rs.getString("codigo_matricula"),
                        rs.getString("discente_id"),
                        rs.getString("disciplina_id"),
                        rs.getDate("data_matricula").toLocalDate()
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            Logger.erro("[MatriculaRepository] Erro ao buscar: " + e.getMessage());
            return null;
        }
    }
}
