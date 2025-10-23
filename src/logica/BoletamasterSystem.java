package logica;

import java.util.ArrayList;
import java.util.List;
import boletamaster.eventos.*;


public class BoletamasterSystem {
    private static BoletamasterSystem instance;
    
    
    private GestorUsuarios gestorUsuarios;
    private GestorEventos gestorEventos;
    private GestorOfertas gestorOfertas;
    private GestorVentas gestorVentas;
    private GestorTiquetes gestorTiquetes;
    private GestorFinanzas gestorFinanzas;
    private GestorReembolsos gestorReembolsos;
    private Reporteador reporteador;
    
    
    private GestorPersistencia gestorPersistencia;
    private ConfiguracionSistema configuracion;
    
    // Datos
    private List<Evento> eventos;
    private List<Venue> venues;
    
    private BoletamasterSystem() {
      
        this.gestorPersistencia = new GestorPersistencia();
        this.configuracion = new ConfiguracionSistema();
        this.gestorFinanzas = new GestorFinanzas();
        
       
        this.gestorVentas = new GestorVentas(gestorFinanzas,gestorOfertas);
        this.gestorEventos = new GestorEventos();
        this.gestorUsuarios = new GestorUsuarios();
        this.gestorTiquetes = new GestorTiquetes();
        this.gestorReembolsos = new GestorReembolsos();
        this.gestorOfertas = new GestorOfertas();
        this.reporteador = new Reporteador(gestorFinanzas);
        this.eventos = new ArrayList<>();
        this.venues = new ArrayList<>();
        
        
        cargarDatosPersistentes();
    }
    
    public static BoletamasterSystem getInstance() {
        if (instance == null) {
            instance = new BoletamasterSystem();
        }
        return instance;
    }
    
    
    private void cargarDatosPersistentes() {
        System.out.println("Cargando datos persistentes...");
        // cargar usuarios, eventos, etc. 
        
    }
    
    public void guardarDatos() {
        System.out.println("Guardando datos...");
        gestorPersistencia.guardarUsuarios(gestorUsuarios.getUsuarios());
        gestorPersistencia.guardarEventos(eventos);
        gestorPersistencia.guardarVenues(venues);
        
    }
    
    // Métodos de acceso a los gestores
    public GestorUsuarios getGestorUsuarios() { return gestorUsuarios; }
    public GestorEventos getGestorEventos() { return gestorEventos; }
    public GestorVentas getGestorVentas() { return gestorVentas; }
    public GestorTiquetes getGestorTiquetes() { return gestorTiquetes; }
    public GestorFinanzas getGestorFinanzas() { return gestorFinanzas; }
    public GestorReembolsos getGestorReembolsos() { return gestorReembolsos; }
    public Reporteador getReporteador() { return reporteador; }
    public GestorOfertas getGestorOfertas() { return gestorOfertas; }
    public GestorPersistencia getGestorPersistencia() { return gestorPersistencia; }
    public ConfiguracionSistema getConfiguracion() { return configuracion; }
    
    // Métodos para gestión de datos
    public void agregarEvento(Evento evento) {
        eventos.add(evento);
    }
    
    public void agregarVenue(Venue venue) {
        venues.add(venue);
    }
    
    public List<Evento> getEventos() { return new ArrayList<>(eventos); }
    public List<Venue> getVenues() { return new ArrayList<>(venues); }
}
