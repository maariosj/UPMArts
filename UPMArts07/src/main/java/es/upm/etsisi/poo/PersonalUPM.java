package es.upm.etsisi.poo;

public class PersonalUPM extends MiembroUPM{
    private int antiguedad;

    public PersonalUPM(String nick, String nombre, String correo, String contrasena, String dni, String tarjetaCredito, int antiguedad) {
        super(nick, nombre, correo, contrasena, dni, tarjetaCredito);
        this.antiguedad = antiguedad;
    }

    public PersonalUPM(String nick, String nombre, String correo, String contrasenaCifrada, boolean yaCifrada, String dni, String tarjetaCredito, int antiguedad) {
        super(nick, nombre, correo, contrasenaCifrada, yaCifrada, dni, tarjetaCredito);
        this.antiguedad = antiguedad;
    }

    public int getAntiguedad() {
        return antiguedad;
    }
    public void setAntiguedad(int antiguedad) {
        this.antiguedad = antiguedad;
    }
    @Override
    public double calcularDescuento() {
        // 25% base + 3% por año, máximo 50%
        double descuento = 0.25 + (0.03 * antiguedad);
        return Math.min(descuento, 0.50);
    }

    @Override
    public String toString() {
        return "PERSONAL;" + super.toString() + ";" + getDni() + ";" + getTarjetaCredito() + ";" + antiguedad;
    }

}
