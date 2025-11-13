package util;

import java.util.Scanner;

public class InputValidator {

    public static int lerInteiroValido(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean confirmarOperacao(Scanner scanner, String mensagem) {
        System.out.print(mensagem + " (S/N): ");
        String resposta = scanner.nextLine().trim().toUpperCase();
        return resposta.equals("S");
    }

    public static boolean isVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static String lerEntradaNaoVazia(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        String entrada = scanner.nextLine().trim();
        return isVazio(entrada) ? null : entrada;
    }
}
