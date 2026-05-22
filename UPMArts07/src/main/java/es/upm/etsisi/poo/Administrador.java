package es.upm.etsisi.poo;

public class Administrador extends Usuario {

    private String telefonoCorporativo;

    public Administrador(String nick, String nombre, String correo, String contrasena, String telefonoCorporativo) {
        super(nick, nombre, correo, contrasena);
        this.telefonoCorporativo = telefonoCorporativo;
    }

    public String getTelefonoCorporativo() {
        return telefonoCorporativo;
    }

    public void setTelefonoCorporativo(String telefonoCorporativo) {
        this.telefonoCorporativo = telefonoCorporativo;
    }

    @Override
    public String toString() {
        return "ADMINISTRADOR;" + super.toString() + ";" + telefonoCorporativo;
    }
}
