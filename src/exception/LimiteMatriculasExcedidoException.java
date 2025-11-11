package exception;

/**
 * Exceção lançada quando um discente tenta se matricular em mais disciplinas
 */
public class LimiteMatriculasExcedidoException extends Exception {
    
    private final int limitePermitido;
    private final int matriculasAtuais;
    
    public LimiteMatriculasExcedidoException(String message, int limitePermitido, int matriculasAtuais) {
        super(message);
        this.limitePermitido = limitePermitido;
        this.matriculasAtuais = matriculasAtuais;
    }
    
    public int getLimitePermitido() {
        return limitePermitido;
    }
    
    public int getMatriculasAtuais() {
        return matriculasAtuais;
    }
}
