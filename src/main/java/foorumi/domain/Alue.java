
package foorumi.domain;

public class Alue {
    
    private int alue_id;
    private String nimi;
    
    public Alue(int alue_id, String nimi) {
        this.alue_id = alue_id;
        this.nimi = nimi;
    }
    
    public int getAlue_id() { return alue_id; }
    
    public String getNimi() { return nimi; }
}
