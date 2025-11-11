package exception;

/**
 * Exceção lançada quando um discente inativo tenta realizar uma operação
 */
public class DiscenteInativoException extends Exception {
    
    public DiscenteInativoException(String message) {
        super(message);
    }
    
    public DiscenteInativoException(String message, Throwable cause) {
        super(message, cause);
    }
}
