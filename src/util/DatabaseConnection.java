package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sistema_academico";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DatabaseConnection] Driver MySQL carregado com sucesso.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] ERRO: Driver MySQL não encontrado!");
            throw new RuntimeException("Falha ao carregar driver MySQL", e);
        }
    }

    /**
     * Construtor público - Dependency Injection.
     * A responsabilidade de garantir instância única agora é do cliente.
     */
    public DatabaseConnection() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("[DatabaseConnection] Banco de dados conectado com sucesso.");
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] ERRO ao conectar ao banco de dados:");
            System.err.println("URL: " + URL);
            System.err.println("USER: " + USER);
            System.err.println("Mensagem: " + e.getMessage());
        }
    }
    
    /**
     * Obtém uma nova conexão com o banco de dados.
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] ERRO ao conectar: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Testa a conexão com o banco de dados.
     */
    public boolean testarConexao() {
        System.out.println("[DatabaseConnection] Testando conexão...");
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("[DatabaseConnection] Conexão estabelecida com sucesso!");
                System.out.println("[DatabaseConnection] Database: " + conn.getCatalog());
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Erro ao testar conexão: " + e.getMessage());
            return false;
        } finally {
            fecharRecursos(conn);
        }
    }
    
    /**
     * Fecha recursos de forma segura.
     */
    public static void fecharRecursos(AutoCloseable... recursos) {
        for (AutoCloseable recurso : recursos) {
            if (recurso != null) {
                try {
                    recurso.close();
                } catch (Exception e) {
                    System.err.println("[DatabaseConnection] Erro ao fechar recurso: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Retorna informações de configuração para debug.
     */
    public String getConfigInfo() {
        return String.format(
            "DatabaseConnection Config:\n" +
            "  URL: %s\n" +
            "  USER: %s\n" +
            "  PASSWORD: ***",
            URL,
            USER
        );
    }
    
    /**
     * Limpa todas as tabelas do banco de dados (TRUNCATE).
     * Usado para resetar dados ao encerrar a aplicação.
     */
    public void limparTodasTabelas() {
        System.out.println("\n[DatabaseConnection] Iniciando limpeza das tabelas...");
        Connection conn = null;
        java.sql.Statement stmt = null;
        
        try {
            conn = getConnection();
            if (conn == null) {
                System.err.println("[DatabaseConnection] Não foi possível obter conexão para limpeza.");
                return;
            }
            
            stmt = conn.createStatement();
            
            // Desabilita verificação de chaves estrangeiras temporariamente
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Trunca as tabelas
            System.out.println("[DatabaseConnection] Limpando tabela 'reservas_livros'...");
            stmt.execute("TRUNCATE TABLE reservas_livros");
            System.out.println("[DatabaseConnection] [OK] Tabela 'reservas_livros' limpa!");
            
            System.out.println("[DatabaseConnection] Limpando tabela 'matriculas'...");
            stmt.execute("TRUNCATE TABLE matriculas");
            System.out.println("[DatabaseConnection] [OK] Tabela 'matriculas' limpa!");
            
            // Reabilita verificação de chaves estrangeiras
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("[DatabaseConnection] [OK] Todas as tabelas foram limpas com sucesso!");
            
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] [ERRO] Falha ao limpar tabelas: " + e.getMessage());
        } finally {
            fecharRecursos(stmt, conn);
        }
    }
}
