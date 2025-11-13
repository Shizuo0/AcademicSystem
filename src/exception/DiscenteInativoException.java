package exception;

public class DiscenteInativoException extends Exception {

    public DiscenteInativoException(String message) {
        super(message);
    }

    public DiscenteInativoException(String message, Throwable cause) {
        super(message, cause);
    }
}
