
package foorumi.domain;

import java.sql.Timestamp;

public class Viesti {
    
    private int viesti_id;
    private Viestiketju viestiketju;
    private Kayttaja kirjoittaja;
    private String aikaleima;
    private String viesti;
    
    public void setViestiketju(Viestiketju viestiketju) {
        this.viestiketju = viestiketju;
    }

    public void setKirjoittaja(Kayttaja kirjoittaja) {
        this.kirjoittaja = kirjoittaja;
    }

    public Viesti(int viesti_id, Viestiketju viestiketju, Kayttaja kirjoittaja, String aikaleima, String viesti) {
        this.viesti_id = viesti_id;
        this.viestiketju = viestiketju;
        this.kirjoittaja = kirjoittaja;
        this.aikaleima = aikaleima;
        this.viesti = viesti;
    }

    public Viesti(int viesti_id, String aikaleima, String viesti) {
        this.viesti_id = viesti_id;
        this.aikaleima = aikaleima;
        this.viesti = viesti;
    }

    public int getViesti_id() {
        return viesti_id;
    }

    public Viestiketju getViestiketju() {
        return viestiketju;
    }

    public Kayttaja getKirjoittaja() {
        return kirjoittaja;
    }

    public String getAikaleima() {
        return aikaleima;
    }

    public String getViesti() {
        return viesti;
    }
    
    
    
}
