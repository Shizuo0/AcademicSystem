package model;

import com.google.gson.annotations.SerializedName;

public class Livro {
    private Long id;
    private String titulo;
    private String autor;
    private Integer ano;

    @SerializedName(value = "statusDisponibilidade", alternate = {"status", "disponibilidade", "disponivel"})
    private StatusDisponibilidade statusDisponibilidade;

    public Livro() {
    }

    public Livro(Long id, String titulo, String autor, Integer ano, StatusDisponibilidade statusDisponibilidade) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.statusDisponibilidade = statusDisponibilidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public StatusDisponibilidade getStatusDisponibilidade() {
        return statusDisponibilidade;
    }

    public void setStatusDisponibilidade(StatusDisponibilidade statusDisponibilidade) {
        this.statusDisponibilidade = statusDisponibilidade;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", ano=" + ano +
                ", statusDisponibilidade=" + statusDisponibilidade +
                '}';
    }
}
