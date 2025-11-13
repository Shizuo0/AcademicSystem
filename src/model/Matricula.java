package model;

import java.time.LocalDate;

public class Matricula {
    private Integer id;
    private String codigoMatricula;
    private String discenteId;
    private String disciplinaId;
    private LocalDate dataMatricula;

    private Matricula(Integer id, String codigoMatricula, String discenteId, String disciplinaId, LocalDate dataMatricula) {
        this.id = id;
        this.codigoMatricula = codigoMatricula;
        this.discenteId = discenteId;
        this.disciplinaId = disciplinaId;
        this.dataMatricula = dataMatricula;
    }

    public static Matricula nova(String codigoMatricula, String discenteId, String disciplinaId, LocalDate dataMatricula) {
        return new Matricula(null, codigoMatricula, discenteId, disciplinaId, dataMatricula);
    }

    public static Matricula doBanco(Integer id, String codigoMatricula, String discenteId, String disciplinaId, LocalDate dataMatricula) {
        return new Matricula(id, codigoMatricula, discenteId, disciplinaId, dataMatricula);
    }

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
