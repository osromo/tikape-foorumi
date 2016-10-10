
package foorumi.database;

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
    private ViestiketjuDao viestiketjuDao;
    private KayttajaDao kayttajaDao;

    public ViestiDao(Database database, ViestiketjuDao viestiketjuDao, KayttajaDao kayttajaDao) {
        this.database = database;
        this.viestiketjuDao = viestiketjuDao;
        this.kayttajaDao = kayttajaDao;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        List<Viesti> viestit = listQuery("WHERE viesti_id =" + key);
        if (!viestit.isEmpty()) { return null; }
        return viestit.get(0);
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        return listQuery("");
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Viesti> findAllWithValue(String attribute, String value) throws SQLException {
        return listQuery("WHERE " + attribute + " = '" + value + "'");
    }

    @Override
    public List<Viesti> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<Viesti> listQuery(String postfix) throws SQLException {
       Connection connection = database.getConnection();
       PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti " + postfix + " ORDER BY aikaleima ASC");
       ResultSet rs = stmt.executeQuery();
       
       Map<Integer, List<Viesti>> viestiKetjut = new HashMap<>();
       List<Viesti> viestit = new ArrayList<>();
       Map<Integer, List<Viesti>> viestiKirjoittajat = new HashMap<>();
       
       
       while (rs.next()) {
           Viesti viesti = new Viesti(rs.getInt("viesti_id"), rs.getString("aikaleima"), rs.getString("viesti"));
           viestit.add(viesti);
           int kirjoittaja = rs.getInt("kirjoittaja");
           if(!viestiKirjoittajat.containsKey(kirjoittaja)) {
               viestiKirjoittajat.put(kirjoittaja, new ArrayList<>()); 
           }
           viestiKirjoittajat.get(kirjoittaja).add(viesti);
           
           int viestiketju = rs.getInt("viestiketju");
           if (!viestiKetjut.containsKey(viestiketju)) { viestiKetjut.put(viestiketju, new ArrayList<>()); }
           viestiKetjut.get(viestiketju).add(viesti);
       }

        for (Viestiketju viestiketju : viestiketjuDao.findAllIn(viestiKetjut.keySet())) {
            for (Viesti viesti : viestiKetjut.get(viestiketju.getViestiketju_id())) {
                viesti.setViestiketju(viestiketju);
            }
        }       
        
        
        for (Kayttaja kirjoittaja : kayttajaDao.findAllIn(viestiKirjoittajat.keySet())) {
            for (Viesti viesti : viestiKirjoittajat.get(kirjoittaja.getKayttaja_id())) {
                viesti.setKirjoittaja(kirjoittaja);
            }
        }       
       rs.close();
       stmt.close();
       connection.close();
       
       return viestit;
    }

    @Override
    public Viesti findOneWithValue(String attribute, String value) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int save(String... args) throws SQLException {
        if (args.length != 3) { return 0; }
        
        Viestiketju viestiketju = viestiketjuDao.findOne(Integer.parseInt(args[0]));
        if (viestiketju == null) { return 0; }
        
        String nimimerkki = args[1];
        Kayttaja kayttaja = kayttajaDao.findOneWithValue("nimimerkki", nimimerkki);
        if (kayttaja == null) {
            kayttajaDao.save(nimimerkki);
            kayttaja = kayttajaDao.findOneWithValue("nimimerkki", nimimerkki);
        }
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti (viestiketju, kirjoittaja, viesti) VALUES (?,?,?)");
        stmt.setInt(1, viestiketju.getViestiketju_id());
        stmt.setInt(2, kayttaja.getKayttaja_id());
        stmt.setString(3, args[2]);
        
        int muutokset = stmt.executeUpdate();
        
        stmt.close();
        connection.close();
        
        return muutokset;
    }
    
}
