package exception;

public class LivroIndisponivelException extends Exception {

    private final String livroId;

    public LivroIndisponivelException(String message, String livroId) {
        super(message);
        this.livroId = livroId;
    }

    public LivroIndisponivelException(String message, String livroId, Throwable cause) {
        super(message, cause);
        this.livroId = livroId;
    }

    public String getLivroId() {
        return livroId;
    }
}
