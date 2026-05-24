package es.upm.etsisi.poo;

public class ParticipanteExterno extends Participante {

    public ParticipanteExterno(String nick, String nombre, String correo, String contrasena, String dni, String tarjetaCredito) {
        super(nick, nombre, correo, contrasena, dni, tarjetaCredito);
    }

    public ParticipanteExterno(String nick, String nombre, String correo, String contrasenaCifrada, boolean yaCifrada, String dni, String tarjetaCredito) {
        super(nick, nombre, correo, contrasenaCifrada, yaCifrada, dni, tarjetaCredito);
    }
    @Override
    public String toString() {
        return "EXTERNO;" + super.toString() + ";" + getDni() + ";" + getTarjetaCredito();
    }
}
