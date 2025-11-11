package util;

/**
 * Classe centralizada para gerenciamento de logs do sistema.
 * Permite controlar verbosidade e formato de saída de logs.
 */
public class Logger {
    
    // Níveis de log
    private static final boolean DEBUG_ENABLED = false; // Logs de debug/cache/tempo
    private static final boolean INFO_ENABLED = true;   // Logs informativos
    private static final boolean ERROR_ENABLED = true;  // Logs de erro
    
    /**
     * Log de debug (cache hits, tempos de requisição, etc).
     * Desabilitado por padrão para não poluir saída.
     */
    public static void debug(String mensagem) {
        if (DEBUG_ENABLED) {
            System.out.println("[DEBUG] " + mensagem);
        }
    }
    
    /**
     * Log informativo (sucesso de operações importantes).
     */
    public static void info(String mensagem) {
        if (INFO_ENABLED) {
            System.out.println("[INFO] " + mensagem);
        }
    }
    
    /**
     * Log de sucesso (operações importantes concluídas).
     */
    public static void sucesso(String mensagem) {
        if (INFO_ENABLED) {
            System.out.println("[OK] " + mensagem);
        }
    }
    
    /**
     * Log de erro (apenas erros críticos).
     */
    public static void erro(String mensagem) {
        if (ERROR_ENABLED) {
            System.err.println("[ERRO] " + mensagem);
        }
    }
    
    /**
     * Log de erro com exceção.
     */
    public static void erro(String mensagem, Exception e) {
        if (ERROR_ENABLED) {
            System.err.println("[ERRO] " + mensagem + ": " + e.getMessage());
        }
    }
    
    /**
     * Log de dica para o usuário.
     */
    public static void dica(String mensagem) {
        if (INFO_ENABLED) {
            System.out.println("[DICA] " + mensagem);
        }
    }
}
