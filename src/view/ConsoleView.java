package view;

import controller.BibliotecaController;
import controller.ControllerFactory;
import controller.DiscenteController;
import controller.DisciplinaController;
import controller.MatriculaController;
import controller.ReservaController;
import model.Discente;
import model.Disciplina;
import model.Livro;
import util.DatabaseConnection;
import util.InputValidator;
import util.TableFormatter;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleView {
    
    private final ControllerFactory controllerFactory;
    private final DiscenteController discenteController;
    private final DisciplinaController disciplinaController;
    private final BibliotecaController bibliotecaController;
    private final MatriculaController matriculaController;
    private final ReservaController reservaController;
    private final Scanner scanner;
    
    /**
     * Construtor padrão - cria os controllers internamente.
     */
    public ConsoleView() {
        System.out.println("═".repeat(60));
        System.out.println("  SISTEMA ACADÊMICO - UNIFOR");
        System.out.println("  Iniciando aplicação...");
        System.out.println("═".repeat(60));
        System.out.println();
        
        try {
            System.out.println("[PROCESSANDO] Inicializando sistema...");
            
            // Criar factory de controllers
            this.controllerFactory = ControllerFactory.criar();
            
            // Obter controllers especializados
            this.discenteController = controllerFactory.getDiscenteController();
            this.disciplinaController = controllerFactory.getDisciplinaController();
            this.bibliotecaController = controllerFactory.getBibliotecaController();
            this.matriculaController = controllerFactory.getMatriculaController();
            this.reservaController = controllerFactory.getReservaController();
            
            this.scanner = new Scanner(System.in);
            
            // Registra shutdown hook para limpar banco de dados ao encerrar
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n═".repeat(60));
                System.out.println("  ENCERRANDO SISTEMA...");
                System.out.println("═".repeat(60));
                controllerFactory.getDatabaseConnection().limparTodasTabelas();
                System.out.println("═".repeat(60));
                System.out.println("  Sistema encerrado. Dados limpos com sucesso!");
                System.out.println("═".repeat(60));
            }, "ShutdownHook-DatabaseCleanup"));
            
            System.out.println("═".repeat(60));
            System.out.println("  Sistema inicializado com sucesso!");
            System.out.println("═".repeat(60));
            
        } catch (Exception e) {
            System.err.println("\n[ERRO] ERRO CRÍTICO ao inicializar sistema:");
            System.err.println("   " + e.getMessage());
            System.err.println("\n[DICA] Verifique:");
            System.err.println("   - Dependências em lib/ (mysql-connector, gson)");
            System.err.println("   - MySQL configurado e rodando");
            System.err.println("   - Microsserviços acessíveis");
            e.printStackTrace();
            throw new RuntimeException("Falha ao inicializar o sistema", e);
        }
    }
    
    /**
     * Construtor alternativo para testes - permite injetar factory mockada.
     */
    public ConsoleView(ControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
        this.discenteController = controllerFactory.getDiscenteController();
        this.disciplinaController = controllerFactory.getDisciplinaController();
        this.bibliotecaController = controllerFactory.getBibliotecaController();
        this.matriculaController = controllerFactory.getMatriculaController();
        this.reservaController = controllerFactory.getReservaController();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Retorna a factory para operações externas.
     */
    public ControllerFactory getControllerFactory() {
        return controllerFactory;
    }
    
    /**
     * Retorna a conexão do banco para operações externas (ex: testes).
     */
    public DatabaseConnection getDatabaseConnection() {
        return controllerFactory.getDatabaseConnection();
    }
    
    public void iniciar() {
        exibirBanner();
        
        boolean executando = true;
        
        while (executando) {
            try {
                exibirMenu();
                int opcao = lerOpcao();
                
                System.out.println();
                
                switch (opcao) {
                    case 1:
                        consultarDiscente();
                        break;
                    case 2:
                        consultarCursoEDisciplina();
                        break;
                    case 3:
                        consultarLivro();
                        break;
                    case 4:
                        realizarMatricula();
                        break;
                    case 5:
                        cancelarMatricula();
                        break;
                    case 6:
                        minhasMatriculas();
                        break;
                    case 7:
                        reservarLivro();
                        break;
                    case 8:
                        cancelarReserva();
                        break;
                    case 9:
                        minhasReservas();
                        break;
                    case 0:
                        executando = false;
                        exibirDespedida();
                        break;
                    default:
                        TableFormatter.imprimirErro("Opção inválida! Escolha um número entre 0 e 9.");
                }
                
                if (executando && opcao != 0) {
                    pausar();
                }
                
            } catch (Exception e) {
                TableFormatter.imprimirErro("Erro inesperado: " + e.getMessage());
                TableFormatter.imprimirDica("Tente novamente ou escolha outra opção.");
                pausar();
            }
        }
        
        scanner.close();
    }
    
    private void exibirBanner() {
        TableFormatter.imprimirBanner(
            "SISTEMA ACADÊMICO - UNIFOR",
            "Gestão de Matrículas e Reservas"
        );
        System.out.println("[OK] Conectado ao MySQL - Dados persistidos!");
        System.out.println();
    }
    
    private void exibirMenu() {
        System.out.println("\n┌────────────────────────────────────────────────┐");
        System.out.println("│               MENU PRINCIPAL                   │");
        System.out.println("├────────────────────────────────────────────────┤");
        System.out.println("│           CONSULTAS                            │");
        System.out.println("│  1. Consultar Discente                         │");
        System.out.println("│  2. Consultar Curso e Disciplina               │");
        System.out.println("│  3. Consultar Livro                            │");
        System.out.println("├────────────────────────────────────────────────┤");
        System.out.println("│           MATRÍCULAS                           │");
        System.out.println("│  4. Matricular em Disciplina                   │");
        System.out.println("│  5. Cancelar Matrícula                         │");
        System.out.println("│  6. Minhas Matrículas                          │");
        System.out.println("├────────────────────────────────────────────────┤");
        System.out.println("│           BIBLIOTECA                           │");
        System.out.println("│  7. Reservar Livro                             │");
        System.out.println("│  8. Cancelar Reserva                           │");
        System.out.println("│  9. Minhas Reservas                            │");
        System.out.println("├────────────────────────────────────────────────┤");
        System.out.println("│  0. Sair                                       │");
        System.out.println("└────────────────────────────────────────────────┘");
        System.out.print("Escolha uma opção: ");
    }
    
    private int lerOpcao() {
        return InputValidator.lerInteiroValido(scanner);
    }
    
    private void consultarDiscente() {
        TableFormatter.imprimirCabecalho("CONSULTAR DISCENTE", 48);
        System.out.println();
        
        System.out.println("[PROCESSANDO] Carregando lista de discentes do microsserviço...");
        
        List<Discente> discentes = discenteController.listarTodosDiscentes();
        
        if (discentes.isEmpty()) {
            TableFormatter.imprimirErro("Nenhum discente disponível no momento!");
            return;
        }
        
        System.out.println("\n[OK] " + discentes.size() + " discentes disponíveis:");
        
        int[] larguras = {6, 28, 21};
        
        TableFormatter.imprimirTopoTabela(larguras);
        TableFormatter.imprimirCabecalhoTabela(larguras, "ID", "Nome", "Curso");
        TableFormatter.imprimirSeparadorTabela(larguras);
        
        // Mostrar apenas os primeiros 15 para não poluir a tela
        int limite = Math.min(discentes.size(), 15);
        for (int i = 0; i < limite; i++) {
            Discente d = discentes.get(i);
            TableFormatter.imprimirLinhaTabela(larguras,
                d.getId(),
                d.getNome(),
                d.getCurso());
        }
        
        TableFormatter.imprimirRodapeTabela(larguras);
        
        if (discentes.size() > 15) {
            System.out.println("... e mais " + (discentes.size() - 15) + " discentes");
        }
        
        System.out.print("\nDigite o ID do discente para ver detalhes (0 para voltar): ");
        String id = scanner.nextLine().trim();
        
        if (id.equals("0") || id.isEmpty()) {
            return;
        }
        
        System.out.println("\n[PROCESSANDO] Buscando detalhes...");
        
        Discente discente = discenteController.consultarDiscente(id);
        
        if (discente == null) {
            TableFormatter.imprimirErro("Discente não encontrado!");
            TableFormatter.imprimirDica("Verifique o ID na lista acima.");
        } else {
            TableFormatter.imprimirCaixaDetalhes("Detalhes do Discente");
            TableFormatter.imprimirLinhaDetalhe("ID", discente.getId());
            TableFormatter.imprimirLinhaDetalhe("Nome", discente.getNome());
            TableFormatter.imprimirLinhaDetalhe("Curso", discente.getCurso());
            TableFormatter.imprimirLinhaDetalhe("Modalidade", discente.getModalidade());
            TableFormatter.imprimirLinhaDetalhe("Status", discente.getSituacaoAcademica());
            TableFormatter.fecharCaixaDetalhes();
            TableFormatter.imprimirDica("Use o ID do discente para realizar matrículas!");
        }
    }
    
    private void consultarCursoEDisciplina() {
        TableFormatter.imprimirCabecalho("CONSULTAR CURSO E DISCIPLINA", 48);
        System.out.println();
        
        System.out.println("[PROCESSANDO] Carregando cursos disponíveis...");
        
        List<String> cursos = disciplinaController.listarCursosDisponiveis();
        
        if (cursos.isEmpty()) {
            TableFormatter.imprimirErro("Nenhum curso encontrado!");
            return;
        }
        
        System.out.println("\n[OK] " + cursos.size() + " cursos disponíveis:");
        
        {
            int[] larguras = {4, 44};
            
            TableFormatter.imprimirTopoTabela(larguras);
            TableFormatter.imprimirCabecalhoTabela(larguras, "Nº", "Curso");
            TableFormatter.imprimirSeparadorTabela(larguras);
            
            for (int i = 0; i < cursos.size(); i++) {
                TableFormatter.imprimirLinhaTabela(larguras, (i + 1), cursos.get(i));
            }
            
            TableFormatter.imprimirRodapeTabela(larguras);
        }
        
        System.out.print("\nDigite o número do curso para ver suas disciplinas (0 para voltar): ");
        String opcao = scanner.nextLine().trim();
        
        if (opcao.equals("0") || opcao.isEmpty()) {
            return;
        }
        
        try {
            int numero = Integer.parseInt(opcao);
            if (numero > 0 && numero <= cursos.size()) {
                String cursoSelecionado = cursos.get(numero - 1);
                
                System.out.println("\n[PROCESSANDO] Carregando disciplinas de " + cursoSelecionado + "...");
                
                List<Disciplina> disciplinas = disciplinaController.listarDisciplinasPorCurso(cursoSelecionado);
                
                if (disciplinas.isEmpty()) {
                    TableFormatter.imprimirErro("Nenhuma disciplina encontrada para este curso!");
                } else {
                    System.out.println("\n[OK] " + disciplinas.size() + " disciplinas do curso " + cursoSelecionado + ":");
                    
                    int[] larguras = {7, 30, 7};
                    
                    TableFormatter.imprimirTopoTabela(larguras);
                    TableFormatter.imprimirCabecalhoTabela(larguras, "ID", "Nome", "Vagas");
                    TableFormatter.imprimirSeparadorTabela(larguras);
                    
                    int limite = Math.min(disciplinas.size(), 15);
                    for (int i = 0; i < limite; i++) {
                        Disciplina d = disciplinas.get(i);
                        TableFormatter.imprimirLinhaTabela(larguras,
                            d.getId(),
                            d.getNome(),
                            d.getVagas());
                    }
                    
                    TableFormatter.imprimirRodapeTabela(larguras);
                    
                    if (disciplinas.size() > 15) {
                        System.out.println("... e mais " + (disciplinas.size() - 15) + " disciplinas");
                    }
                    
                    // Permitir consultar detalhes de uma disciplina
                    System.out.print("\nDigite o ID da disciplina para ver detalhes (0 para voltar): ");
                    String idDisciplina = scanner.nextLine().trim();
                    
                    if (!idDisciplina.equals("0") && !idDisciplina.isEmpty()) {
                        try {
                            int disciplinaId = Integer.parseInt(idDisciplina);
                            Disciplina disciplina = disciplinas.stream()
                                .filter(d -> d.getId() == disciplinaId)
                                .findFirst()
                                .orElse(null);
                            
                            if (disciplina == null) {
                                TableFormatter.imprimirErro("Disciplina não encontrada na lista!");
                                TableFormatter.imprimirDica("Verifique o ID na lista acima.");
                            } else {
                                TableFormatter.imprimirCaixaDetalhes("Detalhes da Disciplina");
                                TableFormatter.imprimirLinhaDetalhe("ID", disciplina.getId());
                                TableFormatter.imprimirLinhaDetalhe("Nome", disciplina.getNome());
                                TableFormatter.imprimirLinhaDetalhe("Curso", disciplina.getCurso());
                                TableFormatter.imprimirLinhaDetalhe("Vagas Disponíveis", disciplina.getVagas());
                                
                                // Indicador visual de disponibilidade
                                String statusVagas;
                                if (disciplina.getVagas() > 10) {
                                    statusVagas = "[OK] DISPONÍVEL (Muitas vagas)";
                                } else if (disciplina.getVagas() > 0) {
                                    statusVagas = "[AVISO] DISPONÍVEL (Poucas vagas)";
                                } else {
                                    statusVagas = "[ERRO] SEM VAGAS";
                                }
                                TableFormatter.imprimirLinhaDetalhe("Status", statusVagas);
                                TableFormatter.fecharCaixaDetalhes();
                                
                                if (disciplina.getVagas() > 0) {
                                    TableFormatter.imprimirDica("Use o ID da disciplina para matricular um discente!");
                                } else {
                                    System.out.println("\n[AVISO] Esta disciplina está com as vagas esgotadas.");
                                }
                            }
                        } catch (NumberFormatException e) {
                            TableFormatter.imprimirErro("ID inválido! Digite apenas números.");
                        }
                    }
                }
            } else {
                TableFormatter.imprimirErro("Número inválido!");
            }
        } catch (NumberFormatException e) {
            TableFormatter.imprimirErro("Entrada inválida!");
        }
    }
    
    private void consultarLivro() {
        TableFormatter.imprimirCabecalho("CONSULTAR LIVRO", 48);
        System.out.println();
        
        System.out.println("[PROCESSANDO] Carregando livros do microsserviço...");
        
        List<Livro> livros = bibliotecaController.listarLivrosDisponiveis();
        
        if (livros.isEmpty()) {
            TableFormatter.imprimirErro("Nenhum livro disponível no momento!");
            return;
        }
        
        System.out.println("\n[OK] " + livros.size() + " livros disponíveis:");
        
        int[] larguras = {7, 40};
        
        TableFormatter.imprimirTopoTabela(larguras);
        TableFormatter.imprimirCabecalhoTabela(larguras, "ID", "Título");
        TableFormatter.imprimirSeparadorTabela(larguras);
        
        int limite = Math.min(livros.size(), 20);
        for (int i = 0; i < limite; i++) {
            Livro l = livros.get(i);
            TableFormatter.imprimirLinhaTabela(larguras,
                l.getId(),
                l.getTitulo());
        }
        
        TableFormatter.imprimirRodapeTabela(larguras);
        
        if (livros.size() > 20) {
            System.out.println("... e mais " + (livros.size() - 20) + " livros");
        }
        
        System.out.print("\nDigite o ID do livro para ver detalhes (0 para voltar): ");
        String id = scanner.nextLine().trim();
        
        if (id.equals("0") || id.isEmpty()) {
            return;
        }
        
        try {
            int livroId = Integer.parseInt(id);
            Livro livro = livros.stream()
                .filter(l -> l.getId() == livroId)
                .findFirst()
                .orElse(null);
            
            if (livro == null) {
                TableFormatter.imprimirErro("Livro não encontrado na lista!");
                TableFormatter.imprimirDica("Verifique o ID na lista acima.");
            } else {
                TableFormatter.imprimirCaixaDetalhes("Detalhes do Livro");
                TableFormatter.imprimirLinhaDetalhe("ID", livro.getId());
                TableFormatter.imprimirLinhaDetalhe("Título", livro.getTitulo());
                TableFormatter.imprimirLinhaDetalhe("Autor", livro.getAutor());
                TableFormatter.imprimirLinhaDetalhe("Ano", livro.getAno());
                
                // Status de disponibilidade
                String statusDisponibilidade;
                if (livro.getStatusDisponibilidade().toString().equals("DISPONIVEL")) {
                    statusDisponibilidade = "[OK] DISPONÍVEL para reserva";
                } else {
                    statusDisponibilidade = "[ERRO] INDISPONÍVEL (Emprestado)";
                }
                TableFormatter.imprimirLinhaDetalhe("Status", statusDisponibilidade);
                TableFormatter.fecharCaixaDetalhes();
                
                if (livro.getStatusDisponibilidade().toString().equals("DISPONIVEL")) {
                    TableFormatter.imprimirDica("Use o ID do livro para fazer uma reserva!");
                } else {
                    System.out.println("\n[AVISO] Este livro está emprestado no momento.");
                }
            }
        } catch (NumberFormatException e) {
            TableFormatter.imprimirErro("ID inválido! Digite apenas números.");
        }
    }
    
    private void realizarMatricula() {
        TableFormatter.imprimirCabecalho("MATRICULAR EM DISCIPLINA", 48);
        System.out.println();
        
        System.out.print("ID do Discente (ex: 3): ");
        String discenteId = scanner.nextLine().trim();
        
        System.out.print("ID da Disciplina (ex: 8374): ");
        String disciplinaId = scanner.nextLine().trim();
        
        if (InputValidator.isVazio(discenteId) || InputValidator.isVazio(disciplinaId)) {
            TableFormatter.imprimirErro("IDs não podem ser vazios!");
            return;
        }
        
        System.out.println("\n[PROCESSANDO] Processando matrícula...");
        System.out.println("   Validando regras de negócio...");
        System.out.println("   Salvando no MySQL...");
        
        boolean sucesso = matriculaController.realizarMatricula(discenteId, disciplinaId);
        
        if (!sucesso) {
            // Service já exibiu mensagem específica
            TableFormatter.imprimirDica("Consulte o discente (opção 1) e liste as disciplinas (opção 2) antes de matricular.");
        }
    }
    
    private void cancelarMatricula() {
        TableFormatter.imprimirCabecalho("CANCELAR MATRÍCULA", 48);
        System.out.println();
        
        System.out.print("Código da Matrícula (ex: 2520001): ");
        String codigoMatricula = scanner.nextLine().trim();
        
        if (InputValidator.isVazio(codigoMatricula)) {
            TableFormatter.imprimirErro("Código não pode ser vazio!");
            return;
        }
        
        if (!InputValidator.confirmarOperacao(scanner, "\n[AVISO] Confirma cancelamento?")) {
            System.out.println("\n[INFO] Cancelamento abortado pelo usuário.");
            return;
        }
        
        System.out.println("\n[PROCESSANDO] Removendo matrícula do MySQL...");
        
        boolean sucesso = matriculaController.cancelarMatriculaPorCodigo(codigoMatricula);
        
        if (!sucesso) {
            TableFormatter.imprimirDica("Consulte suas matrículas (opção 8) para verificar o código correto.");
        }
    }
    
    private void reservarLivro() {
        TableFormatter.imprimirCabecalho("RESERVAR LIVRO", 48);
        System.out.println();
        
        System.out.print("Código de Matrícula (ex: 2520001): ");
        String codigoMatricula = scanner.nextLine().trim();
        
        System.out.print("ID do Livro (ex: 1748): ");
        String livroId = scanner.nextLine().trim();
        
        if (InputValidator.isVazio(codigoMatricula) || InputValidator.isVazio(livroId)) {
            TableFormatter.imprimirErro("Campos não podem ser vazios!");
            return;
        }
        
        System.out.println("\n[PROCESSANDO] Processando reserva...");
        System.out.println("   Verificando disponibilidade...");
        System.out.println("   Registrando simulação...");
        
        boolean sucesso = reservaController.realizarReserva(codigoMatricula, livroId);
        
        if (!sucesso) {
            TableFormatter.imprimirDica("Verifique se o código de matrícula está correto (opção 6) e se o livro está disponível (opção 3).");
        }
    }
    
    private void cancelarReserva() {
        TableFormatter.imprimirCabecalho("CANCELAR RESERVA", 48);
        System.out.println();
        
        System.out.print("Código de Matrícula (ex: 2520001): ");
        String codigoMatricula = scanner.nextLine().trim();
        
        System.out.print("ID do Livro: ");
        String livroId = scanner.nextLine().trim();
        
        if (InputValidator.isVazio(codigoMatricula) || InputValidator.isVazio(livroId)) {
            TableFormatter.imprimirErro("Campos não podem ser vazios!");
            return;
        }
        
        if (!InputValidator.confirmarOperacao(scanner, "\n[AVISO] Confirma cancelamento?")) {
            System.out.println("\n[INFO] Cancelamento abortado pelo usuário.");
            return;
        }
        
        System.out.println("\n[PROCESSANDO] Removendo reserva da simulação...");
        
        boolean sucesso = reservaController.cancelarReserva(codigoMatricula, livroId);
        
        if (!sucesso) {
            System.out.println("\n[DICA] Sugestão: Consulte suas reservas (opção 9) para");
            System.out.println("   verificar o código de matrícula e ID do livro corretos.");
        }
    }
    
    private void minhasMatriculas() {
        TableFormatter.imprimirCabecalho("MINHAS MATRÍCULAS", 48);
        System.out.println();
        
        System.out.print("Digite o ID do discente: ");
        String discenteId = scanner.nextLine().trim();
        
        if (InputValidator.isVazio(discenteId)) {
            TableFormatter.imprimirErro("ID não pode ser vazio!");
            return;
        }
        
        System.out.println("\n[PROCESSANDO] Consultando MySQL + microsserviço...");
        
        List<Map<String, Object>> matriculas = matriculaController.consultarMatriculas(discenteId);
        
        if (matriculas.isEmpty()) {
            System.out.println("\n[INFO] Você não possui matrículas no momento.");
            TableFormatter.imprimirDica("Use a opção 4 para matricular em uma disciplina.");
        } else {
            // Exibir informações do discente (pega da primeira matrícula)
            Map<String, Object> primeiraMatricula = matriculas.get(0);
            System.out.println("\n[DISCENTE]");
            System.out.println("  ID: " + primeiraMatricula.get("discenteId"));
            System.out.println("  Nome: " + primeiraMatricula.get("discenteNome"));
            System.out.println("  Curso: " + primeiraMatricula.get("discenteCurso"));
            
            System.out.println("\n[OK] Suas matrículas (" + matriculas.size() + "/5):");
            
            int[] larguras = {10, 10, 24, 12};
            
            TableFormatter.imprimirTopoTabela(larguras);
            TableFormatter.imprimirCabecalhoTabela(larguras, "Código", "Disc ID", "Disciplina", "Data");
            TableFormatter.imprimirSeparadorTabela(larguras);
            
            for (Map<String, Object> m : matriculas) {
                TableFormatter.imprimirLinhaTabela(larguras,
                    m.get("codigoMatricula"),
                    m.get("disciplinaId"),
                    m.get("disciplinaNome"),
                    m.get("dataMatricula"));
            }
            
            TableFormatter.imprimirRodapeTabela(larguras);
            TableFormatter.imprimirDica("Use o código da matrícula para cancelá-la (opção 5)!");
        }
    }
    
    private void minhasReservas() {
        TableFormatter.imprimirCabecalho("MINHAS RESERVAS", 48);
        System.out.println();
        
        System.out.print("Código de Matrícula (ex: 2520001): ");
        String codigoMatricula = scanner.nextLine().trim();
        
        if (InputValidator.isVazio(codigoMatricula)) {
            TableFormatter.imprimirErro("Código não pode ser vazio!");
            return;
        }
        
        System.out.println("\n[PROCESSANDO] Consultando reservas simuladas...");
        
        List<Map<String, Object>> reservas = reservaController.consultarReservas(codigoMatricula);
        
        if (reservas.isEmpty()) {
            System.out.println("\n[INFO] Você não possui reservas no momento.");
            TableFormatter.imprimirDica("Use a opção 6 para reservar um livro.");
        } else {
            System.out.println("\n[OK] Suas reservas (" + reservas.size() + "):");
            
            int[] larguras = {7, 32, 12};
            
            TableFormatter.imprimirTopoTabela(larguras);
            TableFormatter.imprimirCabecalhoTabela(larguras, "ID", "Título", "Data");
            TableFormatter.imprimirSeparadorTabela(larguras);
            
            for (Map<String, Object> r : reservas) {
                TableFormatter.imprimirLinhaTabela(larguras,
                    r.get("livroId"),
                    r.get("titulo"),
                    r.get("dataReserva"));
            }
            
            TableFormatter.imprimirRodapeTabela(larguras);
        }
    }
    
    private void exibirDespedida() {
        String[] mensagens = {
            "Obrigado por usar o Sistema Acadêmico!",
            "Seus dados estão salvos no MySQL! [OK]"
        };
        TableFormatter.imprimirBanner(mensagens);
    }
    
    private void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
