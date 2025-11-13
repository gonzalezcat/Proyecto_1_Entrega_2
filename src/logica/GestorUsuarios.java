package logica;

import boletamaster.usuarios.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorUsuarios {
    private List<Usuario> usuarios;
    
    public GestorUsuarios() {
        this.usuarios = new ArrayList<>();
        // Usuario administrador por defecto
        usuarios.add(new Administrador("admin", "admin123", "Administrador Principal"));
    }
    
    public Comprador registrarComprador(String login, String password, String nombre) {
        if (buscarUsuarioPorLogin(login).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        Comprador comprador = new Comprador(login, password, nombre);
        usuarios.add(comprador);
        return comprador;
    }
    
    public Organizador registrarOrganizador(String login, String password, String nombre) {
        if (buscarUsuarioPorLogin(login).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        Organizador organizador = new Organizador(login, password, nombre);
        usuarios.add(organizador);
        return organizador;
    }
    
    public Optional<Usuario> buscarUsuarioPorLogin(String login) {
        return usuarios.stream()
                      .filter(u -> u.getLogin().equals(login))
                      .findFirst();
    }
    
    public Usuario autenticar(String login, String password) {
        Optional<Usuario> usuario = buscarUsuarioPorLogin(login);
        if (usuario.isPresent() && usuario.get().checkPassword(password)) {
            return usuario.get();
        }
        throw new SecurityException("Credenciales inválidas");
    }
    
    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }
    public Administrador registrarAdministrador(String login, String password, String nombre) {
        if (buscarUsuarioPorLogin(login).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        Administrador administrador = new Administrador(login, password, nombre);
        usuarios.add(administrador);
        return administrador;
    }
}
