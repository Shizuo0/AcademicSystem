package util;

import java.util.Scanner;

/**
 * Classe utilitária para validação de entrada do usuário.
 */
public class InputValidator {
    
    /**
     * Lê e valida um número inteiro.
     * Retorna -1 se a entrada for inválida.
     */
    public static int lerInteiroValido(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Solicita confirmação do usuário (S/N).
     * Retorna true se a resposta for 'S'.
     */
    public static boolean confirmarOperacao(Scanner scanner, String mensagem) {
        System.out.print(mensagem + " (S/N): ");
        String resposta = scanner.nextLine().trim().toUpperCase();
        return resposta.equals("S");
    }
    
    /**
     * Verifica se uma string é vazia ou nula.
     */
    public static boolean isVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
    
    /**
     * Lê uma entrada não vazia do usuário.
     * Retorna null se a entrada for vazia.
     */
    public static String lerEntradaNaoVazia(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        String entrada = scanner.nextLine().trim();
        return isVazio(entrada) ? null : entrada;
    }
}
