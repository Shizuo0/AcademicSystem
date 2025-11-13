package controller;

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

/**
 * Factory responsável por criar e configurar todos os controllers.
 */
public class ControllerFactory {
    
    private final DiscenteController discenteController;
    private final DisciplinaController disciplinaController;
    private final BibliotecaController bibliotecaController;
    private final MatriculaController matriculaController;
    private final ReservaController reservaController;
    private final DatabaseConnection dbConnection;
    
    /**
     * Construtor privado - cria todas as dependências.
     */
    private ControllerFactory(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        
        // Criar dependências de infraestrutura
        IHttpClient httpClient = new HttpClientImpl();
        
        // Criar services de domínio
        DiscenteService discenteService = new DiscenteService(httpClient);
        DisciplinaService disciplinaService = new DisciplinaService(httpClient);
        BibliotecaService bibliotecaService = new BibliotecaService(httpClient);
        
        // Criar facade
        FacadeService facadeService = new FacadeService(
            discenteService,
            disciplinaService,
            bibliotecaService
        );
        
        // Criar repositories
        IMatriculaRepository matriculaRepository = 
            new MatriculaRepositoryImpl(dbConnection);
        IReservaRepository reservaRepository = 
            new ReservaRepositoryImpl(dbConnection);
        
        // Criar service de disponibilidade
        DisponibilidadeService disponibilidadeService = new DisponibilidadeService(
            discenteService,
            disciplinaService,
            bibliotecaService,
            matriculaRepository,
            reservaRepository
        );
        
        // Criar service de gestão acadêmica
        GestaoAcademicaService gestaoService = new GestaoAcademicaService(
            matriculaRepository,
            reservaRepository,
            disponibilidadeService
        );
        
        // Criar controllers especializados
        this.discenteController = new DiscenteController(facadeService);
        this.disciplinaController = new DisciplinaController(facadeService, disponibilidadeService);
        this.bibliotecaController = new BibliotecaController(facadeService, disponibilidadeService);
        this.matriculaController = new MatriculaController(gestaoService, facadeService);
        this.reservaController = new ReservaController(gestaoService, facadeService);
    }
    
    /**
     * Factory method que cria ControllerFactory com nova DatabaseConnection.
     */
    public static ControllerFactory criar() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        return new ControllerFactory(dbConnection);
    }
    
    /**
     * Factory method que permite injetar DatabaseConnection (para testes).
     */
    public static ControllerFactory criar(DatabaseConnection dbConnection) {
        return new ControllerFactory(dbConnection);
    }
    
    // Getters para os controllers especializados
    
    public DiscenteController getDiscenteController() {
        return discenteController;
    }
    
    public DisciplinaController getDisciplinaController() {
        return disciplinaController;
    }
    
    public BibliotecaController getBibliotecaController() {
        return bibliotecaController;
    }
    
    public MatriculaController getMatriculaController() {
        return matriculaController;
    }
    
    public ReservaController getReservaController() {
        return reservaController;
    }
    
    public DatabaseConnection getDatabaseConnection() {
        return dbConnection;
    }
}
