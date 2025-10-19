package boletamaster.usuarios;

public class Administrador extends Usuario {
    //cannot comprar
    public Administrador(String login, String password, String nombre) {
        super(login, password, nombre);
    }

    @Override
    public String toString() {
        return "Administrador{" + "login=" + login + ", nombre=" + nombre + '}';
    }
}
