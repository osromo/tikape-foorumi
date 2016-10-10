
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

    public ViestiDao(Database database) {
        this.database = database;
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

        for (Viestiketju viestiketju : new ViestiketjuDao(database).findAllIn(viestiKetjut.keySet())) {
            for (Viesti viesti : viestiKetjut.get(viestiketju.getViestiketju_id())) {
                viesti.setViestiketju(viestiketju);
            }
        }       
        
        
        for (Kayttaja kirjoittaja : new KayttajaDao(database).findAllIn(viestiKirjoittajat.keySet())) {
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
    public void save(Viesti viesti) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti (viestiketju, kirjoittaja, viesti) VALUES (?,?,?)");
        stmt.setObject(1, viesti.getViestiketju().getViestiketju_id());
        stmt.setObject(2, viesti.getKirjoittaja().getKayttaja_id());
        stmt.setObject(3, viesti.getViesti());
        
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }
    
}
