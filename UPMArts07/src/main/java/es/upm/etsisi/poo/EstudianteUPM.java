package es.upm.etsisi.poo;

public class EstudianteUPM extends MiembroUPM {
    private String matricula;

    public EstudianteUPM(String nick, String nombre, String correo, String contrasena, String dni, String tarjetaCredito, String matricula) {
        super(nick, nombre, correo, contrasena, dni, tarjetaCredito);
        this.matricula = matricula;
    }
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    @Override
    public double calcularDescuento() {
        return 0.25;
    }

    @Override
    public String toString() {
        return "ESTUDIANTE;" + super.toString() + ";" + getDni() + ";" + getTarjetaCredito() + ";" + matricula;
    }

}
