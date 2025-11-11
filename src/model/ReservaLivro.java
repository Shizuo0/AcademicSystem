package model;

import java.time.LocalDate;

/**
 * Representa uma reserva de livro feita por um discente.
 */
public class ReservaLivro {
    private Integer id; // ID do banco de dados (null antes de persistir)
    private String discenteId; // ID do discente obtido do microsserviço
    private String livroId; // ID do livro obtido do microsserviço
    private LocalDate dataReserva; // Data da reserva
    
    /**
     * Construtor privado - usar factory methods.
     */
    private ReservaLivro(Integer id, String discenteId, String livroId, LocalDate dataReserva) {
        this.id = id;
        this.discenteId = discenteId;
        this.livroId = livroId;
        this.dataReserva = dataReserva;
    }
    
    /**
     * Factory method para criar uma nova reserva (antes de persistir no banco).
     */
    public static ReservaLivro nova(String discenteId, String livroId, LocalDate dataReserva) {
        return new ReservaLivro(null, discenteId, livroId, dataReserva);
    }
    
    /**
     * Factory method para criar uma reserva a partir de dados do banco (com ID).
     */
    public static ReservaLivro doBanco(Integer id, String discenteId, String livroId, LocalDate dataReserva) {
        return new ReservaLivro(id, discenteId, livroId, dataReserva);
    }
    
    // ========================================
    // GETTERS E SETTERS
    // ========================================
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getDiscenteId() {
        return discenteId;
    }
    
    public void setDiscenteId(String discenteId) {
        this.discenteId = discenteId;
    }
    
    public String getLivroId() {
        return livroId;
    }
    
    public void setLivroId(String livroId) {
        this.livroId = livroId;
    }
    
    public LocalDate getDataReserva() {
        return dataReserva;
    }
    
    public void setDataReserva(LocalDate dataReserva) {
        this.dataReserva = dataReserva;
    }
    
    @Override
    public String toString() {
        return "ReservaLivro{" +
                "id=" + id +
                ", discenteId='" + discenteId + '\'' +
                ", livroId='" + livroId + '\'' +
                ", dataReserva=" + dataReserva +
                '}';
    }
}
