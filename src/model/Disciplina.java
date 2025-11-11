package model;

public class Disciplina {
    private Long id;
    private String curso;
    private String nome;
    private Integer vagas;

    public Disciplina() {
    }

    public Disciplina(Long id, String curso, String nome, Integer vagas) {
        this.id = id;
        this.curso = curso;
        this.nome = nome;
        this.vagas = vagas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getVagas() {
        return vagas;
    }

    public void setVagas(Integer vagas) {
        this.vagas = vagas;
    }

    @Override
    public String toString() {
        return "Disciplina{" +
                "id=" + id +
                ", curso='" + curso + '\'' +
                ", nome='" + nome + '\'' +
                ", vagas=" + vagas +
                '}';
    }
}
