package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Logger.debug("[DatabaseConnection] Driver MySQL carregado com sucesso.");
        } catch (ClassNotFoundException e) {
            Logger.erro("[DatabaseConnection] Driver MySQL não encontrado!");
            throw new RuntimeException("Falha ao carregar driver MySQL", e);
        }
    }

    public DatabaseConnection() {
        try (Connection conn = DriverManager.getConnection(
                EnvironmentConfig.getDatabaseUrl(),
                EnvironmentConfig.getDatabaseUser(),
                EnvironmentConfig.getDatabasePassword())) {
            Logger.debug("[DatabaseConnection] Banco de dados conectado com sucesso.");
        } catch (SQLException e) {
            Logger.erro("[DatabaseConnection] Erro ao conectar ao banco de dados:");
            Logger.erro("URL: " + EnvironmentConfig.getDatabaseUrl());
            Logger.erro("USER: " + EnvironmentConfig.getDatabaseUser());
            Logger.erro("Mensagem: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    EnvironmentConfig.getDatabaseUrl(),
                    EnvironmentConfig.getDatabaseUser(),
                    EnvironmentConfig.getDatabasePassword());
        } catch (SQLException e) {
            Logger.erro("[DatabaseConnection] Erro ao conectar: " + e.getMessage());
            return null;
        }
    }

    public static void fecharRecursos(AutoCloseable... recursos) {
        for (AutoCloseable recurso : recursos) {
            if (recurso != null) {
                try {
                    recurso.close();
                } catch (Exception e) {
                    Logger.erro("[DatabaseConnection] Erro ao fechar recurso: " + e.getMessage());
                }
            }
        }
    }

    public void limparTodasTabelas() {
        Logger.sistema("\n" + "═".repeat(60));
        Logger.sistema("ENCERRANDO SISTEMA...");
        Logger.sistema("═".repeat(60));
        Logger.info("Iniciando limpeza das tabelas...");

        Connection conn = null;
        java.sql.Statement stmt = null;

        try {
            conn = getConnection();
            if (conn == null) {
                Logger.erro("Não foi possível obter conexão para limpeza.");
                return;
            }

            stmt = conn.createStatement();

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            Logger.debug("Limpando tabela 'reservas_livros'...");
            stmt.execute("TRUNCATE TABLE reservas_livros");

            Logger.debug("Limpando tabela 'matriculas'...");
            stmt.execute("TRUNCATE TABLE matriculas");

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            Logger.sucesso("Todas as tabelas foram limpas com sucesso!");
            Logger.sistema("═".repeat(60));
            Logger.sistema("Sistema encerrado. Dados limpos!");
            Logger.sistema("═".repeat(60));

        } catch (SQLException e) {
            Logger.erro("Falha ao limpar tabelas: " + e.getMessage());
        } finally {
            fecharRecursos(stmt, conn);
        }
    }
}
