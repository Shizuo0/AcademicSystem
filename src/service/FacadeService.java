package service;

import model.Discente;
import model.Disciplina;
import model.Livro;

import java.util.List;

public class FacadeService {
    
    private final DiscenteService discenteService;
    private final DisciplinaService disciplinaService;
    private final BibliotecaService bibliotecaService;
    
    /**
     * Construtor com injeção de dependências.
     */
    public FacadeService(
            DiscenteService discenteService,
            DisciplinaService disciplinaService,
            BibliotecaService bibliotecaService) {
        
        this.discenteService = discenteService;
        this.disciplinaService = disciplinaService;
        this.bibliotecaService = bibliotecaService;
    }
    
    // === DISCENTES ===
    
    public Discente buscarDiscente(Long id) {
        return discenteService.buscarPorId(id);
    }
    
    public List<Discente> listarDiscentes() {
        return discenteService.listarTodos();
    }
    
    // === DISCIPLINAS ===
    
    public Disciplina buscarDisciplina(Long id) {
        return disciplinaService.buscarPorId(id);
    }
    
    public List<Disciplina> listarDisciplinas() {
        return disciplinaService.listarTodas();
    }
    
    public List<Disciplina> listarDisciplinasPorCurso(String curso) {
        return disciplinaService.filtrarPorCurso(curso);
    }
    
    // === LIVROS ===
    
    public Livro buscarLivro(Long id) {
        return bibliotecaService.buscarPorId(id);
    }
    
    public List<Livro> listarLivros() {
        return bibliotecaService.listarTodos();
    }
}
