
package foorumi.domain;

public class Kayttaja {
    
    private int kayttaja_id;
    private String nimimerkki;

    public Kayttaja(int kayttaja_id, String nimimerkki) {
        this.kayttaja_id = kayttaja_id;
        this.nimimerkki = nimimerkki;
    }

    public int getKayttaja_id() {
        return kayttaja_id;
    }

    public String getNimimerkki() {
        return nimimerkki;
    }
    
    
    
}
