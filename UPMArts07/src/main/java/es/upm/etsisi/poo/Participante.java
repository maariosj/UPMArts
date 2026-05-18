package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;

public abstract class Participante  extends Usuario{
    private String dni;
    private String tarjetaCredito;
    private List<PreferenciaArtistica> preferencias;
    public Participante(String nick, String nombre, String correo, String contrasena, String dni, String tarjetaCredito) {
        super(nick, nombre, correo, contrasena);
        this.dni = dni;
        this.tarjetaCredito = tarjetaCredito;
        this.preferencias = new ArrayList<>();
    }
    public String getDni() {
        return dni;
    }

    public String getTarjetaCredito() {
        return tarjetaCredito;
    }
   public List<PreferenciaArtistica> getPreferencias() {
        return preferencias;
   }
   public void anadirPreferencia(PreferenciaArtistica preferencia){
        preferencias.add(preferencia);
   }

}
