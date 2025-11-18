package util;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentConfig {

    private static final Dotenv dotenv;

    static {
        dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        
        Logger.debug("[EnvironmentConfig] Configurações carregadas do .env");
    }

    public static String getDatabaseUrl() {
        return dotenv.get("DB_URL", "jdbc:mysql://localhost:3306/sistema_academico");
    }

    public static String getDatabaseUser() {
        return dotenv.get("DB_USER", "root");
    }

    public static String getDatabasePassword() {
        return dotenv.get("DB_PASSWORD", "12345678");
    }
}
