package controller;

import model.Discente;
import model.Disciplina;
import model.Livro;
import model.Matricula;
import model.ReservaLivro;
import model.StatusDisponibilidade;
import repository.IMatriculaRepository;
import repository.IReservaRepository;
import repository.MatriculaRepositoryImpl;
import repository.ReservaRepositoryImpl;
import service.BibliotecaService;
import service.DiscenteService;
import service.DisciplinaService;
import service.DisponibilidadeService;
import service.FacadeService;
import service.GestaoAcademicaService;
import util.DatabaseConnection;
import util.HttpClientImpl;
import util.IHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SistemaController {
    
    private final GestaoAcademicaService gestaoService;
    private final FacadeService facadeService;
    private final DisponibilidadeService disponibilidadeService;
    private final DatabaseConnection dbConnection;
    
    public SistemaController(
            GestaoAcademicaService gestaoService,
            FacadeService facadeService,
            DisponibilidadeService disponibilidadeService,
            DatabaseConnection dbConnection) {
        
        this.gestaoService = gestaoService;
        this.facadeService = facadeService;
        this.disponibilidadeService = disponibilidadeService;
        this.dbConnection = dbConnection;
    }
    
    /**
     * Retorna a instância de DatabaseConnection para operações de cleanup.
     */
    public DatabaseConnection getDatabaseConnection() {
        return dbConnection;
    }
    
    /**
     * Factory method que cria todas as dependências com Dependency Injection.
     * Cria a instância única de DatabaseConnection internamente.
     */
    public static SistemaController criar() {
        // Criar instância única de DatabaseConnection (gerenciada pelo Controller)
        DatabaseConnection dbConnection = new DatabaseConnection();
        
        IHttpClient httpClient = new HttpClientImpl();
        
        DiscenteService discenteService = new DiscenteService(httpClient);
        DisciplinaService disciplinaService = new DisciplinaService(httpClient);
        BibliotecaService bibliotecaService = new BibliotecaService(httpClient);
        
        FacadeService facadeService = new FacadeService(
            discenteService,
            disciplinaService,
            bibliotecaService
        );
        
        IMatriculaRepository matriculaRepository = 
            new MatriculaRepositoryImpl(dbConnection);
        IReservaRepository reservaRepository = 
            new ReservaRepositoryImpl(dbConnection);
        
        DisponibilidadeService disponibilidadeService = new DisponibilidadeService(
            discenteService,
            disciplinaService,
            bibliotecaService,
            matriculaRepository,
            reservaRepository
        );
        
        GestaoAcademicaService gestaoService = new GestaoAcademicaService(
            matriculaRepository,
            reservaRepository,
            disponibilidadeService
        );
        
        return new SistemaController(
            gestaoService,
            facadeService,
            disponibilidadeService,
            dbConnection
        );
    }
    
    /**
     * Factory method alternativo para testes - permite injetar DatabaseConnection mockada.
     */
    public static SistemaController criar(DatabaseConnection dbConnection) {
        IHttpClient httpClient = new HttpClientImpl();
        
        DiscenteService discenteService = new DiscenteService(httpClient);
        DisciplinaService disciplinaService = new DisciplinaService(httpClient);
        BibliotecaService bibliotecaService = new BibliotecaService(httpClient);
        
        FacadeService facadeService = new FacadeService(
            discenteService,
            disciplinaService,
            bibliotecaService
        );
        
        IMatriculaRepository matriculaRepository = 
            new MatriculaRepositoryImpl(dbConnection);
        IReservaRepository reservaRepository = 
            new ReservaRepositoryImpl(dbConnection);
        
        DisponibilidadeService disponibilidadeService = new DisponibilidadeService(
            discenteService,
            disciplinaService,
            bibliotecaService,
            matriculaRepository,
            reservaRepository
        );
        
        GestaoAcademicaService gestaoService = new GestaoAcademicaService(
            matriculaRepository,
            reservaRepository,
            disponibilidadeService
        );
        
        return new SistemaController(
            gestaoService,
            facadeService,
            disponibilidadeService,
            dbConnection
        );
    }
    
    public Discente consultarDiscente(String id) {
        try {
            Long idLong = Long.parseLong(id);
            return facadeService.buscarDiscente(idLong);
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<Discente> listarTodosDiscentes() {
        try {
            return facadeService.listarDiscentes();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    public List<String> listarCursosDisponiveis() {
        try {
            List<Disciplina> disciplinas = facadeService.listarDisciplinas();
            return disciplinas.stream()
                    .map(Disciplina::getCurso)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    public List<Disciplina> listarDisciplinasPorCurso(String curso) {
        try {
            List<Disciplina> disciplinas;
            
            if (curso == null || curso.trim().isEmpty()) {
                disciplinas = facadeService.listarDisciplinas();
            } else {
                disciplinas = facadeService.listarDisciplinasPorCurso(curso);
            }
            for (Disciplina disciplina : disciplinas) {
                int vagasReais = disponibilidadeService.calcularVagasDisponiveis(
                    String.valueOf(disciplina.getId())
                );
                disciplina.setVagas(vagasReais);
            }
            
            return disciplinas;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    public List<Livro> listarLivrosDisponiveis() {
        try {
            List<Livro> todosLivros = facadeService.listarLivros();
            return todosLivros.stream()
                    .filter(livro -> {
                        if (livro.getStatusDisponibilidade() != StatusDisponibilidade.DISPONIVEL) {
                            return false;
                        }
                        return disponibilidadeService.verificarLivroDisponivel(
                            String.valueOf(livro.getId())
                        );
                    })
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    public boolean realizarMatricula(String discenteId, String disciplinaId) {
        try {
            return gestaoService.simularMatricula(discenteId, disciplinaId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean cancelarMatricula(String discenteId, String disciplinaId) {
        try {
            return gestaoService.cancelarMatricula(discenteId, disciplinaId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean cancelarMatriculaPorCodigo(String codigoMatricula) {
        try {
            return gestaoService.cancelarMatriculaPorCodigo(codigoMatricula);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean realizarReserva(String discenteId, String livroId) {
        try {
            return gestaoService.simularReservaLivro(discenteId, livroId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean realizarReservaPorCodigo(String codigoMatricula, String livroId) {
        try {
            return gestaoService.simularReservaLivroPorCodigo(codigoMatricula, livroId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean cancelarReserva(String discenteId, String livroId) {
        try {
            return gestaoService.cancelarReservaLivro(discenteId, livroId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean cancelarReservaPorCodigo(String codigoMatricula, String livroId) {
        try {
            return gestaoService.cancelarReservaLivroPorCodigo(codigoMatricula, livroId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<Map<String, Object>> consultarMatriculas(String discenteId) {
        try {
            List<Matricula> matriculas = gestaoService.listarMatriculasDiscente(discenteId);
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            for (Matricula matricula : matriculas) {
                Map<String, Object> info = new HashMap<>();
                try {
                    Long disciplinaId = Long.parseLong(matricula.getDisciplinaId());
                    Disciplina disciplina = facadeService.buscarDisciplina(disciplinaId);
                    
                    if (disciplina != null) {
                        info.put("codigoMatricula", matricula.getCodigoMatricula());
                        info.put("disciplinaId", matricula.getDisciplinaId());
                        info.put("disciplinaNome", disciplina.getNome());
                        info.put("curso", disciplina.getCurso());
                        info.put("dataMatricula", matricula.getDataMatricula());
                    } else {
                        info.put("codigoMatricula", matricula.getCodigoMatricula());
                        info.put("disciplinaId", matricula.getDisciplinaId());
                        info.put("disciplinaNome", "Disciplina não encontrada");
                        info.put("curso", "N/A");
                        info.put("dataMatricula", matricula.getDataMatricula());
                    }
                } catch (Exception e) {
                    info.put("codigoMatricula", matricula.getCodigoMatricula());
                    info.put("disciplinaId", matricula.getDisciplinaId());
                    info.put("disciplinaNome", "Erro ao buscar dados");
                    info.put("curso", "N/A");
                    info.put("dataMatricula", matricula.getDataMatricula());
                }
                
                resultado.add(info);
            }
            
            return resultado;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    public List<Map<String, Object>> consultarReservas(String discenteId) {
        try {
            List<ReservaLivro> reservas = gestaoService.listarReservasDiscente(discenteId);
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            for (ReservaLivro reserva : reservas) {
                Map<String, Object> info = new HashMap<>();
                try {
                    Long livroId = Long.parseLong(reserva.getLivroId());
                    Livro livro = facadeService.buscarLivro(livroId);
                    
                    if (livro != null) {
                        info.put("livroId", reserva.getLivroId());
                        info.put("titulo", livro.getTitulo());
                        info.put("autor", livro.getAutor());
                        info.put("dataReserva", reserva.getDataReserva());
                    } else {
                        info.put("livroId", reserva.getLivroId());
                        info.put("titulo", "Livro não encontrado");
                        info.put("autor", "N/A");
                        info.put("dataReserva", reserva.getDataReserva());
                    }
                } catch (Exception e) {
                    info.put("livroId", reserva.getLivroId());
                    info.put("titulo", "Erro ao buscar dados");
                    info.put("autor", "N/A");
                    info.put("dataReserva", reserva.getDataReserva());
                }
                
                resultado.add(info);
            }
            
            return resultado;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    public List<Map<String, Object>> consultarReservasPorCodigo(String codigoMatricula) {
        try {
            // Buscar matrícula pelo código
            Matricula matricula = gestaoService.buscarMatriculaPorCodigo(codigoMatricula);
            if (matricula == null) {
                return Collections.emptyList();
            }
            
            // Usar o discenteId da matrícula para buscar reservas
            return consultarReservas(matricula.getDiscenteId());
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
