package exception;

public class SemVagasException extends Exception {

    private final String disciplinaId;
    private final int vagasDisponiveis;

    public SemVagasException(String message, String disciplinaId, int vagasDisponiveis) {
        super(message);
        this.disciplinaId = disciplinaId;
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public String getDisciplinaId() {
        return disciplinaId;
    }

    public int getVagasDisponiveis() {
        return vagasDisponiveis;
    }
}
