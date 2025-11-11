package util;

import java.time.LocalDate;

public class GeradorMatricula {
    
    public static String gerar(int ano, int semestre, int ordem) {
        // Validações
        if (ano < 2000 || ano > 2099) {
            throw new IllegalArgumentException("Ano deve estar entre 2000 e 2099");
        }
        
        if (semestre != 1 && semestre != 2) {
            throw new IllegalArgumentException("Semestre deve ser 1 ou 2");
        }
        
        if (ordem < 1 || ordem > 9999) {
            throw new IllegalArgumentException("Ordem deve estar entre 1 e 9999");
        }
        
        // Extrair últimos 2 dígitos do ano
        int anoAbreviado = ano % 100; // 2025 -> 25
        
        // Formatar: AA + S + NNNN
        String matricula = String.format("%02d%d%04d", anoAbreviado, semestre, ordem);
        
        return matricula;
    }
    
    /**
     * Gera um código de matrícula usando o ano e semestre atual.
     */
    public static String gerarComDataAtual(int ordem) {
        if (ordem < 1 || ordem > 9999) {
            throw new IllegalArgumentException("Ordem deve estar entre 1 e 9999, recebido: " + ordem);
        }
        
        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        
        // Determinar semestre baseado no mês
        // Janeiro a Junho = Semestre 1
        // Julho a Dezembro = Semestre 2
        int mes = hoje.getMonthValue();
        int semestre = (mes <= 6) ? 1 : 2;
        
        return gerar(ano, semestre, ordem);
    }
    
    /**
     * Extrai o ano de um código de matrícula.
     */
    public static int extrairAno(String matricula) {
        validarFormato(matricula);
        
        int anoAbreviado = Integer.parseInt(matricula.substring(0, 2));
        
        // Assumir século 2000 se ano >= 00
        return 2000 + anoAbreviado;
    }
    
    /**
     * Extrai o semestre de um código de matrícula.
     */
    public static int extrairSemestre(String matricula) {
        validarFormato(matricula);
        return Integer.parseInt(matricula.substring(2, 3));
    }
    
    /**
     * Extrai o número de ordem de um código de matrícula.
     */
    public static int extrairOrdem(String matricula) {
        validarFormato(matricula);
        return Integer.parseInt(matricula.substring(3, 7));
    }
    
    /**
     * Valida se uma matrícula está no formato correto.
     */
    public static boolean validar(String matricula) {
        if (matricula == null || matricula.length() != 7) {
            return false;
        }
        
        // Verificar se é numérico
        if (!matricula.matches("\\d{7}")) {
            return false;
        }
        
        try {
            // Extrair sem validação para evitar recursão
            int anoAbreviado = Integer.parseInt(matricula.substring(0, 2));
            int ano = 2000 + anoAbreviado;
            int semestre = Integer.parseInt(matricula.substring(2, 3));
            int ordem = Integer.parseInt(matricula.substring(3, 7));
            
            // Validar ranges
            return ano >= 2000 && ano <= 2099 &&
                   (semestre == 1 || semestre == 2) &&
                   ordem >= 1 && ordem <= 9999;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Formata uma matrícula para exibição legível.
     */
    public static String formatar(String matricula) {
        validarFormato(matricula);
        
        int ano = extrairAno(matricula);
        int semestre = extrairSemestre(matricula);
        int ordem = extrairOrdem(matricula);
        
        return String.format("%d.%d - #%04d", ano, semestre, ordem);
    }
    
    /**
     * Valida o formato da matrícula e lança exceção se inválida.
     */
    private static void validarFormato(String matricula) {
        if (!validar(matricula)) {
            throw new IllegalArgumentException(
                "Matrícula inválida. Formato esperado: AASNNNN (ex: 2510001)"
            );
        }
    }
}
