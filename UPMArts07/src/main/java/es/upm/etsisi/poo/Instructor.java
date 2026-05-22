package es.upm.etsisi.poo;

public class Instructor extends Usuario {
    private String dni;
    private String iban;

    public Instructor(String nick, String nombre, String correo, String contrasena, String dni, String iban) {
        super(nick, nombre, correo, contrasena);
        this.dni = dni;
        this.iban = iban;
    }
    public Instructor(String nick, String nombre, String correo, String contrasenaCifrada, boolean yaCifrada) {
        super(nick, nombre, correo, contrasenaCifrada);
        setContrasenaCifradaDirectamente(contrasenaCifrada);
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public String getIban() {
        return iban;
    }
    public void setIban(String iban) {
        this.iban = iban;
    }
    @Override
    public String toString() {
        return "INSTRUCTOR;" + super.toString() + ";" + dni + ";" + iban;
    }

}
