package util;

/**
 * Classe utilitária para formatação de textos e tabelas no console.
 */
public class TableFormatter {
    
    private static final int LARGURA_PADRAO = 48;
    
    /**
     * Trunca um texto para caber no tamanho especificado.
     */
    public static String truncar(String texto, int tamanho) {
        if (texto == null) return "";
        if (texto.length() <= tamanho) return texto;
        return texto.substring(0, tamanho - 3) + "...";
    }
    
    /**
     * Imprime uma linha de separação.
     */
    public static void imprimirLinha(int tamanho) {
        System.out.println("═".repeat(tamanho));
    }
    
    /**
     * Imprime um cabeçalho centralizado com bordas duplas.
     */
    public static void imprimirCabecalho(String titulo, int largura) {
        System.out.println("╔" + "═".repeat(largura) + "╗");
        int espacos = (largura - titulo.length()) / 2;
        System.out.println("║" + " ".repeat(espacos) + titulo + 
                          " ".repeat(largura - espacos - titulo.length()) + "║");
        System.out.println("╚" + "═".repeat(largura) + "╝");
    }
    
    /**
     * Imprime um cabeçalho centralizado com largura padrão (48).
     */
    public static void imprimirCabecalho(String titulo) {
        imprimirCabecalho(titulo, LARGURA_PADRAO);
    }
    
    /**
     * Imprime uma caixa de detalhes com título e conteúdo.
     */
    public static void imprimirCaixaDetalhes(String titulo) {
        System.out.println("\n[OK] " + titulo + ":");
        System.out.println("┌" + "─".repeat(LARGURA_PADRAO) + "┐");
    }
    
    /**
     * Imprime uma linha de detalhe dentro da caixa.
     */
    public static void imprimirLinhaDetalhe(String campo, Object valor) {
        String texto = String.format("%s: %s", campo, valor);
        System.out.printf("│ %-" + (LARGURA_PADRAO - 2) + "s │%n", texto);
    }
    
    /**
     * Fecha a caixa de detalhes.
     */
    public static void fecharCaixaDetalhes() {
        System.out.println("└" + "─".repeat(LARGURA_PADRAO) + "┘");
    }
    
    /**
     * Imprime o topo de uma tabela com colunas.
     */
    public static void imprimirTopoTabela(int... largurasColunas) {
        System.out.print("┌");
        for (int i = 0; i < largurasColunas.length; i++) {
            System.out.print("─".repeat(largurasColunas[i]));
            if (i < largurasColunas.length - 1) {
                System.out.print("┬");
            }
        }
        System.out.println("┐");
    }
    
    /**
     * Imprime o cabeçalho de uma tabela.
     */
    public static void imprimirCabecalhoTabela(int[] largurasColunas, String... titulos) {
        System.out.print("│");
        for (int i = 0; i < titulos.length; i++) {
            System.out.printf(" %-" + (largurasColunas[i] - 2) + "s │", titulos[i]);
        }
        System.out.println();
    }
    
    /**
     * Imprime o separador entre cabeçalho e conteúdo da tabela.
     */
    public static void imprimirSeparadorTabela(int... largurasColunas) {
        System.out.print("├");
        for (int i = 0; i < largurasColunas.length; i++) {
            System.out.print("─".repeat(largurasColunas[i]));
            if (i < largurasColunas.length - 1) {
                System.out.print("┼");
            }
        }
        System.out.println("┤");
    }
    
    /**
     * Imprime o rodapé de uma tabela.
     */
    public static void imprimirRodapeTabela(int... largurasColunas) {
        System.out.print("└");
        for (int i = 0; i < largurasColunas.length; i++) {
            System.out.print("─".repeat(largurasColunas[i]));
            if (i < largurasColunas.length - 1) {
                System.out.print("┴");
            }
        }
        System.out.println("┘");
    }
    
    /**
     * Formata uma célula de tabela com largura fixa.
     */
    public static String formatarCelula(Object valor, int largura) {
        String texto = valor != null ? String.valueOf(valor) : "";
        return String.format("%-" + largura + "s", truncar(texto, largura));
    }
    
    /**
     * Imprime uma linha de célula em uma tabela.
     */
    public static void imprimirLinhaTabela(int[] largurasColunas, Object... valores) {
        System.out.print("│");
        for (int i = 0; i < valores.length; i++) {
            String valor = valores[i] != null ? String.valueOf(valores[i]) : "";
            System.out.printf(" %-" + (largurasColunas[i] - 2) + "s │", 
                truncar(valor, largurasColunas[i] - 2));
        }
        System.out.println();
    }
    
    /**
     * Imprime um banner com bordas duplas e múltiplas linhas.
     */
    public static void imprimirBanner(String... linhas) {
        int larguraMaxima = 0;
        for (String linha : linhas) {
            larguraMaxima = Math.max(larguraMaxima, linha.length());
        }
        
        System.out.println("\n╔" + "═".repeat(larguraMaxima + 2) + "╗");
        for (String linha : linhas) {
            int espacos = larguraMaxima - linha.length();
            System.out.println("║ " + linha + " ".repeat(espacos) + " ║");
        }
        System.out.println("╚" + "═".repeat(larguraMaxima + 2) + "╝");
    }
    
    /**
     * Imprime uma mensagem de sucesso formatada.
     */
    public static void imprimirSucesso(String mensagem) {
        System.out.println("\n[OK] " + mensagem);
    }
    
    /**
     * Imprime uma mensagem de erro formatada.
     */
    public static void imprimirErro(String mensagem) {
        System.out.println("\n[ERRO] " + mensagem);
    }
    
    /**
     * Imprime uma dica formatada.
     */
    public static void imprimirDica(String mensagem) {
        System.out.println("\n[DICA] " + mensagem);
    }
}
