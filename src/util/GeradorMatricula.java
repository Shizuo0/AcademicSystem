package util;

import java.time.LocalDate;

public class GeradorMatricula {

    public static String gerar(int ano, int semestre, int ordem) {
        if (ano < 2000 || ano > 2099) {
            throw new IllegalArgumentException("Ano deve estar entre 2000 e 2099");
        }

        if (semestre != 1 && semestre != 2) {
            throw new IllegalArgumentException("Semestre deve ser 1 ou 2");
        }

        if (ordem < 1 || ordem > 9999) {
            throw new IllegalArgumentException("Ordem deve estar entre 1 e 9999");
        }

        int anoAbreviado = ano % 100;

        String matricula = String.format("%02d%d%04d", anoAbreviado, semestre, ordem);

        return matricula;
    }

    public static String gerarComDataAtual(int ordem) {
        if (ordem < 1 || ordem > 9999) {
            throw new IllegalArgumentException("Ordem deve estar entre 1 e 9999, recebido: " + ordem);
        }

        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();

        int mes = hoje.getMonthValue();
        int semestre = (mes <= 6) ? 1 : 2;

        return gerar(ano, semestre, ordem);
    }

    public static int extrairAno(String matricula) {
        validarFormato(matricula);

        int anoAbreviado = Integer.parseInt(matricula.substring(0, 2));

        return 2000 + anoAbreviado;
    }

    public static int extrairSemestre(String matricula) {
        validarFormato(matricula);
        return Integer.parseInt(matricula.substring(2, 3));
    }

    public static int extrairOrdem(String matricula) {
        validarFormato(matricula);
        return Integer.parseInt(matricula.substring(3, 7));
    }

    public static boolean validar(String matricula) {
        if (matricula == null || matricula.length() != 7) {
            return false;
        }

        if (!matricula.matches("\\d{7}")) {
            return false;
        }

        try {
            int anoAbreviado = Integer.parseInt(matricula.substring(0, 2));
            int ano = 2000 + anoAbreviado;
            int semestre = Integer.parseInt(matricula.substring(2, 3));
            int ordem = Integer.parseInt(matricula.substring(3, 7));

            return ano >= 2000 && ano <= 2099 &&
                   (semestre == 1 || semestre == 2) &&
                   ordem >= 1 && ordem <= 9999;
        } catch (Exception e) {
            return false;
        }
    }

    public static String formatar(String matricula) {
        validarFormato(matricula);

        int ano = extrairAno(matricula);
        int semestre = extrairSemestre(matricula);
        int ordem = extrairOrdem(matricula);

        return String.format("%d.%d - #%04d", ano, semestre, ordem);
    }

    private static void validarFormato(String matricula) {
        if (!validar(matricula)) {
            throw new IllegalArgumentException(
                "Matrícula inválida. Formato esperado: AASNNNN (ex: 2510001)"
            );
        }
    }
}
