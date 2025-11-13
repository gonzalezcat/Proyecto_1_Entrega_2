package test;

import logica.*;
import boletamaster.usuarios.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorUsuariosTest {
    private GestorUsuarios gestorUsuarios;

    @BeforeEach
    void setUp() {
        gestorUsuarios = new GestorUsuarios();
    }

    @Test
    void testRegistrarCompradorExitoso() {
       
        Comprador comprador = gestorUsuarios.registrarComprador("nuevoUser", "pass123", "Juan Pérez");
        assertNotNull(comprador);
        assertEquals("nuevoUser", comprador.getLogin());
        assertEquals("Juan Pérez", comprador.getNombre());
    }

    @Test
    void testRegistrarCompradorConLoginExistente() {
        gestorUsuarios.registrarComprador("usuarioExistente", "pass123", "Usuario Existente");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gestorUsuarios.registrarComprador("usuarioExistente", "otraPass", "Otro Usuario");
        });
        
        assertEquals("El usuario ya existe", exception.getMessage());
    }

    @Test
    void testAutenticarExitoso() {
        gestorUsuarios.registrarComprador("testUser", "testPass", "Test User");
        Usuario usuario = gestorUsuarios.autenticar("testUser", "testPass");
        assertNotNull(usuario);
        assertEquals("testUser", usuario.getLogin());
    }

    @Test
    void testAutenticarCredencialesInvalidas() {
        gestorUsuarios.registrarComprador("testUser", "testPass", "Test User");
        assertThrows(SecurityException.class, () -> {
            gestorUsuarios.autenticar("testUser", "passwordIncorrecta");
        });
    }

    @Test
    void testBuscarUsuarioPorLoginExistente() {
        gestorUsuarios.registrarComprador("usuarioBuscar", "pass123", "Usuario a Buscar");
        var usuarioOpt = gestorUsuarios.buscarUsuarioPorLogin("usuarioBuscar");
        assertTrue(usuarioOpt.isPresent());
        assertEquals("usuarioBuscar", usuarioOpt.get().getLogin());
    }

    @Test
    void testBuscarUsuarioPorLoginNoExistente() {
    
        var usuarioOpt = gestorUsuarios.buscarUsuarioPorLogin("noExiste");
        assertTrue(usuarioOpt.isEmpty());
    }
}