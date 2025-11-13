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

public class ControllerFactory {

    private final DiscenteController discenteController;
    private final DisciplinaController disciplinaController;
    private final BibliotecaController bibliotecaController;
    private final MatriculaController matriculaController;
    private final ReservaController reservaController;
    private final DatabaseConnection dbConnection;
    private final FacadeService facadeService;

    private ControllerFactory(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;

        IHttpClient httpClient = new HttpClientImpl();

        DiscenteService discenteService = new DiscenteService(httpClient);
        DisciplinaService disciplinaService = new DisciplinaService(httpClient);
        BibliotecaService bibliotecaService = new BibliotecaService(httpClient);

        this.facadeService = new FacadeService(
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

        this.discenteController = new DiscenteController(facadeService);
        this.disciplinaController = new DisciplinaController(facadeService, disponibilidadeService);
        this.bibliotecaController = new BibliotecaController(facadeService, disponibilidadeService);
        this.matriculaController = new MatriculaController(gestaoService, facadeService);
        this.reservaController = new ReservaController(gestaoService, facadeService);
    }

    public void inicializarCaches() {
        facadeService.inicializarCaches();
    }

    public static ControllerFactory criar() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        return new ControllerFactory(dbConnection);
    }

    public static ControllerFactory criar(DatabaseConnection dbConnection) {
        return new ControllerFactory(dbConnection);
    }

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
