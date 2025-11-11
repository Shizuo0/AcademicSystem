package exception;

/**
 * Exceção lançada quando um livro não está disponível para reserva.
 */
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
