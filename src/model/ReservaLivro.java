package model;

import java.time.LocalDate;

public class ReservaLivro {
    private Integer id;
    private String discenteId;
    private String livroId;
    private LocalDate dataReserva;

    private ReservaLivro(Integer id, String discenteId, String livroId, LocalDate dataReserva) {
        this.id = id;
        this.discenteId = discenteId;
        this.livroId = livroId;
        this.dataReserva = dataReserva;
    }

    public static ReservaLivro nova(String discenteId, String livroId, LocalDate dataReserva) {
        return new ReservaLivro(null, discenteId, livroId, dataReserva);
    }

    public static ReservaLivro doBanco(Integer id, String discenteId, String livroId, LocalDate dataReserva) {
        return new ReservaLivro(id, discenteId, livroId, dataReserva);
    }

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
