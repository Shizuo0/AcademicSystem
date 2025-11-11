package repository;

import model.ReservaLivro;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaRepositoryImpl implements IReservaRepository {
    
    private final DatabaseConnection databaseConnection;
    
    // SQL Statements
    private static final String SQL_INSERT = 
        "INSERT INTO reservas_livros (discente_id, livro_id, data_reserva) VALUES (?, ?, ?)";
    
    private static final String SQL_DELETE = 
        "DELETE FROM reservas_livros WHERE discente_id = ? AND livro_id = ?";
    
    private static final String SQL_SELECT_BY_DISCENTE = 
        "SELECT id, discente_id, livro_id, data_reserva FROM reservas_livros WHERE discente_id = ?";
    
    private static final String SQL_LIVRO_RESERVADO = 
        "SELECT COUNT(*) FROM reservas_livros WHERE livro_id = ?";
    
    private static final String SQL_EXISTS = 
        "SELECT COUNT(*) FROM reservas_livros WHERE discente_id = ? AND livro_id = ?";
    
    public ReservaRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    
    @Override
    public boolean adicionar(ReservaLivro reserva) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            
            stmt.setString(1, reserva.getDiscenteId());
            stmt.setString(2, reserva.getLivroId());
            stmt.setDate(3, Date.valueOf(reserva.getDataReserva()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("[ReservaRepository] Reserva duplicada");
            } else {
                System.err.println("[ReservaRepository] Erro SQL: " + e.getMessage());
            }
            return false;
        }
    }
    
    @Override
    public boolean remover(String discenteId, String livroId) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            
            stmt.setString(1, discenteId);
            stmt.setString(2, livroId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("[ReservaRepository] Reserva não encontrada");
            }
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("[ReservaRepository] Erro ao remover: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<ReservaLivro> listarPorDiscente(String discenteId) {
        List<ReservaLivro> reservas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseConnection.getConnection();
            if (conn == null) {
                System.err.println("[ReservaRepository] Falha ao obter conexão com o banco.");
                return reservas;
            }
            
            stmt = conn.prepareStatement(SQL_SELECT_BY_DISCENTE);
            stmt.setString(1, discenteId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ReservaLivro reserva = ReservaLivro.doBanco(
                    rs.getInt("id"),
                    rs.getString("discente_id"),
                    rs.getString("livro_id"),
                    rs.getDate("data_reserva").toLocalDate()
                );
                reservas.add(reserva);
            }
            
        } catch (SQLException e) {
            System.err.println("[ReservaRepository] Erro ao listar reservas: " + e.getMessage());
            
        } finally {
            DatabaseConnection.fecharRecursos(rs, stmt, conn);
        }
        
        return reservas;
    }
    
    @Override
    public boolean livroEstaReservado(String livroId) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_LIVRO_RESERVADO)) {
            
            stmt.setString(1, livroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("[ReservaRepository] Erro ao verificar: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean existeReserva(String discenteId, String livroId) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS)) {
            
            stmt.setString(1, discenteId);
            stmt.setString(2, livroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("[ReservaRepository] Erro ao verificar: " + e.getMessage());
            return false;
        }
    }
}
