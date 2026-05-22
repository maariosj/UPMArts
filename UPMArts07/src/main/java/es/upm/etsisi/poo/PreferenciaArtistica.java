package es.upm.etsisi.poo;

public class PreferenciaArtistica {

    private int nivelExp;
    private DiscpArt tipPreferencia;

    public PreferenciaArtistica(int nivelExp, DiscpArt tipPreferencia) {
        this.nivelExp = nivelExp;
        this.tipPreferencia = tipPreferencia;
    }

    public int getNivelExp() {
        return nivelExp;
    }

    public void setNivelExp(int nivelExp) {
        this.nivelExp = nivelExp;
    }

    public DiscpArt getTipPreferencia() {
        return tipPreferencia;
    }

    public void setTipPreferencia(DiscpArt tipPreferencia) {
        this.tipPreferencia = tipPreferencia;
    }

    @Override
    public String toString() {
        return tipPreferencia + ";" + nivelExp;
    }
}