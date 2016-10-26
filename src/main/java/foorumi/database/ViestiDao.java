
package foorumi.database;

import foorumi.domain.Alue;
import foorumi.domain.Kayttaja;
import foorumi.domain.Viesti;
import foorumi.domain.Viestiketju;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViestiDao implements Dao<Viesti, Integer> {
    
    private Database database;
    private AlueDao alueDao;
    private ViestiketjuDao viestiketjuDao;
    private KayttajaDao kayttajaDao;

    public ViestiDao(Database database, AlueDao alueDao, ViestiketjuDao viestiketjuDao, KayttajaDao kayttajaDao) {
        this.database = database;
        this.alueDao = alueDao;
        this.viestiketjuDao = viestiketjuDao;
        this.kayttajaDao = kayttajaDao;
    }
    
    @Override
    public Viesti findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Viesti> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Viesti> findAllInViestiketju(Viestiketju viestiketju) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE viestiketju = ? ORDER BY aikaleima ASC");
        stmt.setInt(1, viestiketju.getViestiketju_id());
        
        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        Map<Integer, List<Viesti>> viestitKirjoittajanMukaan = new HashMap<>();
        while (rs.next()) {
            Viesti viesti = new Viesti(rs.getInt("viesti_id"), viestiketju, null, rs.getString("aikaleima"), rs.getString("viesti"));
            viestit.add(viesti);
            int kirjoittaja = rs.getInt("kirjoittaja");
            if (!viestitKirjoittajanMukaan.containsKey(kirjoittaja)) { viestitKirjoittajanMukaan.put(kirjoittaja, new ArrayList<>()); }
            viestitKirjoittajanMukaan.get(kirjoittaja).add(viesti);
        }
        
        for (Kayttaja kirjoittaja : kayttajaDao.findAllIn(viestitKirjoittajanMukaan.keySet())) {
            for (Viesti viesti : viestitKirjoittajanMukaan.get(kirjoittaja.getKayttaja_id())) {
                viesti.setKirjoittaja(kirjoittaja);
            }
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return viestit;
    }
    
    public Map<Integer, String> findLatestAikaleimaPerViestiketju(Collection<Viestiketju> viestiketjut) throws SQLException {
        if (viestiketjut.isEmpty()) { return new HashMap<>(); }
        
        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < viestiketjut.size(); i++) { muuttujat.append(", ?"); }
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT viestiketju, MAX(aikaleima) AS viimeisin FROM Viesti WHERE viestiketju IN (" + muuttujat + ") GROUP BY viestiketju ORDER BY viimeisin DESC");
        int i = 1; for (Viestiketju viestiketju : viestiketjut) { stmt.setInt(i, viestiketju.getViestiketju_id()); i++; }
        
        ResultSet rs = stmt.executeQuery();
        Map<Integer, String> viimeisinAikaleimaViestiketjunMukaan = new HashMap<>();
        while (rs.next()) { viimeisinAikaleimaViestiketjunMukaan.put(rs.getInt("viestiketju"), rs.getString("viimeisin")); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return viimeisinAikaleimaViestiketjunMukaan;
    }
    
    public Map<Integer, String> findLatestAikaleimaPerAlue(Collection<Alue> alueet) throws SQLException {
        Collection<Viestiketju> viestiketjut = viestiketjuDao.findAllWithAlueIn(alueet);
        
        Map<Integer, String> viimeisinAikaleimaViestiketjunMukaan = findLatestAikaleimaPerViestiketju(viestiketjut);
        Map<Integer, String> viimeisinAikaleimaAlueenMukaan = new HashMap<>();
        for (Alue alue : alueet) { viimeisinAikaleimaAlueenMukaan.put(alue.getAlue_id(), null); }
        
        
        
        
        return null;
    }

    @Override
    public void create(Viesti viesti) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti (viestiketju, kirjoittaja, viesti) VALUES (?,?,?)");
        stmt.setInt(1, viesti.getViestiketju().getViestiketju_id());
        stmt.setInt(2, viesti.getKirjoittaja().getKayttaja_id());
        stmt.setString(3, viesti.getViesti());
        
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
