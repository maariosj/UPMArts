package es.upm.etsisi.poo;

public abstract class MiembroUPM extends Participante {

    public MiembroUPM(String nick, String nombre, String correo, String contrasena, String dni, String tarjetaCredito) {
        super(nick, nombre, correo, contrasena, dni, tarjetaCredito);
    }
    public abstract double calcularDescuento();
}
