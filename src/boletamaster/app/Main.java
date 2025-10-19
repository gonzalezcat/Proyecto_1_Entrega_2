package boletamaster.app;

import boletamaster.usuarios.*;
import boletamaster.eventos.*;
import boletamaster.tiquetes.*;
import boletamaster.transacciones.*;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Sistema s = new Sistema();

        // crear usuarios
        Organizador org = new Organizador("org1", "passOrg", "Organizador Uno");
        Comprador cata = new Comprador("cata", "cata123", "Catalina");
        Comprador juan = new Comprador("juan", "juanpass", "Juan");
        Administrador admin = new Administrador("admin", "adminpass", "Admin");

        s.registrarUsuario(org);
        s.registrarUsuario(cata);
        s.registrarUsuario(juan);
        s.registrarUsuario(admin);

        // venue y evento
        Venue v = new Venue("v1", "Estadio Central", "CIudad X", 50000, true);
        Evento concierto = new Evento("e1", "Gran Concierto", LocalDateTime.now().plusDays(30), v, org);
        Localidad gramilla = new Localidad("l1", "Gramilla", 50.0, 1000, false);
        Localidad palcos = new Localidad("l2", "Palcos", 200.0, 100, true);
        palcos.addAsiento("A1"); palcos.addAsiento("A2");

        concierto.addLocalidad(gramilla);
        concierto.addLocalidad(palcos);

        s.registrarVenue(v);
        s.registrarEvento(concierto);

        // generar tickets
        Ticket t1 = s.generarTicketSimpleParaLocalidad(gramilla);
        Ticket t2 = s.generarTicketNumeradoParaLocalidad(palcos, "A1");
        TicketMultiple paquete = s.generarTicketMultiple(gramilla, 3, 120.0);
        TicketDeluxe td = s.generarTicketDeluxe(palcos);

        System.out.println("Tickets generados:");
        System.out.println(t1);
        System.out.println(t2);
        System.out.println(paquete);
        System.out.println(td);

        // comprar tickets
        cata.depositarSaldo(1000.0);
        Compra compra1 = s.comprarTicket(cata, t1);
        System.out.println("Compra1: " + compra1);

        Compra compra2 = s.comprarTicket(juan, t2);
        System.out.println("Compra2: " + compra2);

        // intentar transferir deluxe  esto falla
        try {
            s.transferirTicket(td, cata, "cata123", juan);
            System.out.println("Transferencia deluxe (unexpected) ok");
        } catch (Exception ex) {
            System.out.println("Transferencia deluxe falló como esperado: " + ex.getMessage());
        }

        // transferir ticket simple
        try {
            s.transferirTicket(t1, cata, "cata123", juan);
            System.out.println("Transferencia t1 exitosa. Nuevo dueño: " + t1.getPropietario().getLogin());
        } catch (Exception ex) {
            System.out.println("Transferencia t1 falló: " + ex.getMessage());
        }

        // comprar paquete multiple
        Compra compraPaquete = s.comprarTicket(juan, paquete);
        System.out.println("Compra paquete: " + compraPaquete);

        // transferir paquete completo
        try {
            s.transferirTicket(paquete, juan, "juanpass", cata);
            System.out.println("Paquete transferido a " + paquete.getPropietario().getLogin());
        } catch (Exception ex) {
            System.out.println("Transferir paquete falló: " + ex.getMessage());
        }

        // cancelar evento y reembolsar
        List<Reembolso> reembolsos = s.cancelarEventoYReembolsar(concierto, admin);
        System.out.println("Reembolsos generados: " + reembolsos.size());
        for (Reembolso r : reembolsos) System.out.println(r);

        // reporte
        s.reporteFinancieroPorOrganizador(org);

        System.out.println("Estado final de usuarios:");
        System.out.println("Cata saldo: " + cata.getSaldo());
        System.out.println("Juan saldo: " + juan.getSaldo());
    }
}

