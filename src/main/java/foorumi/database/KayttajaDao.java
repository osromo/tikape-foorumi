
package foorumi.database;

import foorumi.domain.Kayttaja;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KayttajaDao implements Dao<Kayttaja, Integer> {
    
    private Database database;

    public KayttajaDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Kayttaja findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Kayttaja> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Kayttaja> findAllIn(Collection<Integer> keys) throws SQLException {
        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE kayttaja_id in ("+muuttujat+")");
        int i = 1; for (Integer key : keys) { stmt.setInt(i, key); i++; }
        
        ResultSet rs = stmt.executeQuery();
        List<Kayttaja> kayttajat = new ArrayList<>();
        while (rs.next()) {kayttajat.add(new Kayttaja(rs.getInt("kayttaja_id"), rs.getString("nimimerkki"))); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return kayttajat;
    }

    public Kayttaja findWithNimimerkki(String nimimerkki) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE nimimerkki = ?");
        stmt.setString(1, nimimerkki);
        
        ResultSet rs = stmt.executeQuery();
        Kayttaja kayttaja = null;
        if (rs.next()) { kayttaja = new Kayttaja(rs.getInt("kayttaja_id"), nimimerkki); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return kayttaja;
    }

    @Override
    public void create(Kayttaja kayttaja) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Kayttaja (nimimerkki) VALUES (?)");
        stmt.setString(1, kayttaja.getNimimerkki());
        
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
