package model;

import com.google.gson.annotations.SerializedName;

public class Discente {
    private Long id;
    private String nome;
    private String curso;
    private String modalidade;
    
    @SerializedName(value = "situacaoAcademica", alternate = {"situacao", "status"})
    private SituacaoAcademica situacaoAcademica;

    public Discente() {
    }

    public Discente(Long id, String nome, String curso, String modalidade, SituacaoAcademica situacaoAcademica) {
        this.id = id;
        this.nome = nome;
        this.curso = curso;
        this.modalidade = modalidade;
        this.situacaoAcademica = situacaoAcademica;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public SituacaoAcademica getSituacaoAcademica() {
        return situacaoAcademica;
    }

    public void setSituacaoAcademica(SituacaoAcademica situacaoAcademica) {
        this.situacaoAcademica = situacaoAcademica;
    }

    @Override
    public String toString() {
        return "Discente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", curso='" + curso + '\'' +
                ", modalidade='" + modalidade + '\'' +
                ", situacaoAcademica=" + situacaoAcademica +
                '}';
    }
}
