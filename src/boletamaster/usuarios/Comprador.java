package boletamaster.usuarios;

public class Comprador extends Usuario {
    public Comprador(String login, String password, String nombre) {
        super(login, password, nombre);
    }

    @Override
    public String toString() {
        return "Comprador{" + "login=" + login + ", nombre=" + nombre + ", saldo=" + saldo + '}';
    }
}
