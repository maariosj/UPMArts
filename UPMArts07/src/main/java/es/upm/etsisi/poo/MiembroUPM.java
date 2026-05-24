package es.upm.etsisi.poo;

public abstract class MiembroUPM extends Participante {

    public MiembroUPM(String nick, String nombre, String correo, String contrasena, String dni, String tarjetaCredito) {
        super(nick, nombre, correo, contrasena, dni, tarjetaCredito);
    }

    public MiembroUPM(String nick, String nombre, String correo, String contrasenaCifrada, boolean yaCifrada, String dni, String tarjetaCredito) {
        super(nick, nombre, correo, contrasenaCifrada, yaCifrada, dni, tarjetaCredito);
    }
    public abstract double calcularDescuento();
}
