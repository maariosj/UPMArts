package es.upm.etsisi.poo;

public class ParticipanteExterno extends Participante {
    public ParticipanteExterno(String nick, String nombre, String correo, String contrasena, String dni, String tarjetaCredito) {
        super(nick, nombre, correo, contrasena, dni, tarjetaCredito);
    }
    @Override
    public String toString() {
        return "EXTERNO;" + super.toString() + ";" + getDni() + ";" + getTarjetaCredito();
    }
}
