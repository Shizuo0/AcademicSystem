package util;

public class Logger {

    private static final boolean DEBUG_ENABLED = false;
    private static final boolean INFO_ENABLED = true;
    private static final boolean ERROR_ENABLED = true;

    public static void debug(String mensagem) {
        if (DEBUG_ENABLED) {
            System.out.println("[DEBUG] " + mensagem);
        }
    }

    public static void info(String mensagem) {
        if (INFO_ENABLED) {
            System.out.println("[INFO] " + mensagem);
        }
    }

    public static void sucesso(String mensagem) {
        if (INFO_ENABLED) {
            System.out.println("[OK] " + mensagem);
        }
    }

    public static void erro(String mensagem) {
        if (ERROR_ENABLED) {
            System.err.println("[ERRO] " + mensagem);
        }
    }

    public static void dica(String mensagem) {
        if (INFO_ENABLED) {
            System.out.println("[DICA] " + mensagem);
        }
    }

    public static void aviso(String mensagem) {
        if (INFO_ENABLED) {
            System.out.println("\u001B[33m[AVISO] " + mensagem + "\u001B[0m");
        }
    }

    public static void sistema(String mensagem) {
        if (INFO_ENABLED) {
            System.out.println("[SISTEMA] " + mensagem);
        }
    }
}
