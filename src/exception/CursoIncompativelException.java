package exception;

/**
 * Exceção lançada quando um discente tenta se matricular em uma disciplina
 */
public class CursoIncompativelException extends Exception {
    
    private final String cursoDisciplina;
    private final String cursoDiscente;
    
    public CursoIncompativelException(String message, String cursoDisciplina, String cursoDiscente) {
        super(message);
        this.cursoDisciplina = cursoDisciplina;
        this.cursoDiscente = cursoDiscente;
    }
    
    public String getCursoDisciplina() {
        return cursoDisciplina;
    }
    
    public String getCursoDiscente() {
        return cursoDiscente;
    }
}
