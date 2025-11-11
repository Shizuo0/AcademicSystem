package model;

import java.time.LocalDate;

/**
 * Representa uma matrícula de um discente em uma disciplina.
 */
public class Matricula {
    private Integer id; // ID do banco de dados (null antes de persistir)
    private String codigoMatricula; // Código único da matrícula (formato: AASNNNN)
    private String discenteId; // ID do discente obtido do microsserviço
    private String disciplinaId; // ID da disciplina obtido do microsserviço
    private LocalDate dataMatricula; // Data da matrícula
    
    /**
     * Construtor privado - usar factory methods.
     */
    private Matricula(Integer id, String codigoMatricula, String discenteId, String disciplinaId, LocalDate dataMatricula) {
        this.id = id;
        this.codigoMatricula = codigoMatricula;
        this.discenteId = discenteId;
        this.disciplinaId = disciplinaId;
        this.dataMatricula = dataMatricula;
    }
    
    /**
     * Factory method para criar uma nova matrícula (antes de persistir no banco).
     */
    public static Matricula nova(String codigoMatricula, String discenteId, String disciplinaId, LocalDate dataMatricula) {
        return new Matricula(null, codigoMatricula, discenteId, disciplinaId, dataMatricula);
    }
    
    /**
     * Factory method para criar uma matrícula a partir de dados do banco (com ID).
     */
    public static Matricula doBanco(Integer id, String codigoMatricula, String discenteId, String disciplinaId, LocalDate dataMatricula) {
        return new Matricula(id, codigoMatricula, discenteId, disciplinaId, dataMatricula);
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
    
    public String getCodigoMatricula() {
        return codigoMatricula;
    }
    
    public void setCodigoMatricula(String codigoMatricula) {
        this.codigoMatricula = codigoMatricula;
    }
    
    public String getDiscenteId() {
        return discenteId;
    }
    
    public void setDiscenteId(String discenteId) {
        this.discenteId = discenteId;
    }
    
    public String getDisciplinaId() {
        return disciplinaId;
    }
    
    public void setDisciplinaId(String disciplinaId) {
        this.disciplinaId = disciplinaId;
    }
    
    public LocalDate getDataMatricula() {
        return dataMatricula;
    }
    
    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }
    
    @Override
    public String toString() {
        return "Matricula{" +
                "id=" + id +
                ", codigoMatricula='" + codigoMatricula + '\'' +
                ", discenteId='" + discenteId + '\'' +
                ", disciplinaId='" + disciplinaId + '\'' +
                ", dataMatricula=" + dataMatricula +
                '}';
    }
}
