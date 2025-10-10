package boletamaster.usuarios;

public class Organizador extends Usuario {
    public Organizador(String login, String password, String nombre) {
        super(login, password, nombre);
    }

    @Override
    public String toString() {
        return "Organizador{" + "login=" + login + ", nombre=" + nombre + '}';
    }
}