package test;

import logica.*;

import boletamaster.eventos.*;
import boletamaster.usuarios.Organizador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class GestorOfertasTest {
    private GestorOfertas gestorOfertas;
    private Organizador organizador;
    private Localidad localidad;

    @BeforeEach
    void setUp() {
        gestorOfertas = new GestorOfertas();
        organizador = new Organizador("org1", "pass123", "Organizador Test");
        localidad = new Localidad("L001", "General", 100.0, 1000, false);
    }

    @Test
    void testCrearOfertaExitoso() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = LocalDateTime.now().plusDays(7);
        double porcentajeDescuento = 0.2; // 20%
        Oferta oferta = gestorOfertas.crearOferta(organizador, localidad, porcentajeDescuento, inicio, fin);
        assertNotNull(oferta);
        assertEquals(localidad, oferta.getLocalidad());
        assertEquals(porcentajeDescuento, oferta.getPorcentajeDescuento());
        assertEquals(inicio, oferta.getInicio());
        assertEquals(fin, oferta.getFin());
    }

    @Test
    void testCrearOfertaConDescuentoInvalido() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = LocalDateTime.now().plusDays(7);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gestorOfertas.crearOferta(organizador, localidad, 0.6, inicio, fin);
        });
        
        assertEquals("El descuento debe estar entre 1% y 50%", exception.getMessage());
    }

    @Test
    void testCrearOfertaConFechasInvalidas() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(7);
        LocalDateTime fin = LocalDateTime.now().plusDays(1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gestorOfertas.crearOferta(organizador, localidad, 0.2, inicio, fin);
        });
        
        assertEquals("La fecha de fin debe ser posterior a la de inicio", exception.getMessage());
    }

    @Test
    void testObtenerOfertaVigente() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1); // Empezó ayer
        LocalDateTime fin = LocalDateTime.now().plusDays(1);     // Termina mañana
        
        Oferta oferta = new Oferta(localidad, 0.2, inicio, fin);
        
        // Usar el método auxiliar para evitar validaciones
        gestorOfertas.agregarOfertaParaTesting(oferta);
        System.out.println("=== DEBUG ===");
        System.out.println("Localidad ID: " + localidad.getId());
        System.out.println("Oferta Localidad ID: " + oferta.getLocalidad().getId());
        System.out.println("¿Localidades iguales? " + localidad.equals(oferta.getLocalidad()));
        System.out.println("¿Oferta vigente? " + oferta.estaVigente());
        System.out.println("¿Oferta activa? " + oferta.isActiva());
        System.out.println("Ofertas en gestor: " + gestorOfertas.getOfertas().size());
        
        // Verificar el filtrado paso a paso
        Oferta resultado = gestorOfertas.getOfertas().stream()
                .filter(o -> {
                    boolean localidadOk = o.getLocalidad().equals(localidad);
                    boolean vigenteOk = o.estaVigente();
                    System.out.println("Filtro - Localidad: " + localidadOk + ", Vigente: " + vigenteOk);
                    return localidadOk && vigenteOk;
                })
                .findFirst()
                .orElse(null);
        System.out.println("Resultado del stream: " + resultado);
        Oferta ofertaVigente = gestorOfertas.obtenerOfertaVigente(localidad);
        assertNotNull(ofertaVigente, "No se encontró oferta vigente");
        assertEquals(oferta, ofertaVigente);
    }
}
