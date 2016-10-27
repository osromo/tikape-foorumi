
package foorumi.domain;

public class Osiotieto {
    
    private int osio_id;
    private String osio_nimi;
    private int osio_viesteja;
    private String osio_viimeisinviesti;

    public Osiotieto(int osio_id, String osio_nimi, int osio_viesteja, String osio_viimeisinviesti) {
        this.osio_id = osio_id;
        this.osio_nimi = osio_nimi;
        this.osio_viesteja = osio_viesteja;
        this.osio_viimeisinviesti = osio_viimeisinviesti;
    }

    public int getOsio_id() {
        return osio_id;
    }

    public String getOsio_nimi() {
        return osio_nimi;
    }

    public int getOsio_viesteja() {
        return osio_viesteja;
    }

    public String getOsio_viimeisinviesti() {
        return osio_viimeisinviesti;
    }
    
    
    
}
