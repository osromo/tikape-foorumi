
package foorumi.domain;

import java.util.Map;

public class Viestiketju {
    
    private int viestiketju_id;
    private Alue alue;
    private String otsikko;
    
    public Viestiketju(int viestiketju_id, Alue alue, String otsikko) {
        this.viestiketju_id = viestiketju_id;
        this.alue = alue;
        this.otsikko = otsikko;
    }

    public Viestiketju(int viestiketju_id, String otsikko) {
        this.viestiketju_id = viestiketju_id;
        this.otsikko = otsikko;
    }

    public int getViestiketju_id() {
        return viestiketju_id;
    }

    public Alue getAlue() {
        return alue;
    }

    public String getOtsikko() {
        return otsikko;
    }
    
    public void setAlue(Alue alue) {
        this.alue = alue;
    }
    
}
