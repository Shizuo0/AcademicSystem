import view.ConsoleView;

/**
 * Classe principal do sistema acadêmico.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("═".repeat(60));
        System.out.println("  SISTEMA ACADÊMICO - UNIFOR");
        System.out.println("  Iniciando aplicação...");
        System.out.println("═".repeat(60));
        System.out.println();

        try {
            System.out.println("[PROCESSANDO] Inicializando sistema...");
            
            // Criar view
            ConsoleView view = new ConsoleView();
            
            // Registra shutdown hook para limpar banco de dados ao encerrar
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n═".repeat(60));
                System.out.println("  ENCERRANDO SISTEMA...");
                System.out.println("═".repeat(60));
                view.getController().getDatabaseConnection().limparTodasTabelas();
                System.out.println("═".repeat(60));
                System.out.println("  Sistema encerrado. Dados limpos com sucesso!");
                System.out.println("═".repeat(60));
            }, "ShutdownHook-DatabaseCleanup"));
            
            System.out.println("═".repeat(60));
            System.out.println("  Sistema inicializado com sucesso!");
            System.out.println("═".repeat(60));
            
            view.iniciar();
            
        } catch (Exception e) {
            System.err.println("\n[ERRO] ERRO CRÍTICO ao inicializar sistema:");
            System.err.println("   " + e.getMessage());
            System.err.println("\n[DICA] Verifique:");
            System.err.println("   - Dependências em lib/ (mysql-connector, gson)");
            System.err.println("   - MySQL configurado e rodando");
            System.err.println("   - Microsserviços acessíveis");
            e.printStackTrace();
        }
    }
}
