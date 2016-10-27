
package foorumi.database;

import foorumi.domain.Alue;
import foorumi.domain.Osiotieto;
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

public class AlueDao implements Dao<Alue, Integer> {
    
    private Database database;

    public AlueDao(Database database) {
        this.database = database;
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE alue_id = ?");
        stmt.setInt(1, key);
        
        ResultSet rs = stmt.executeQuery();
        Alue alue = null;
        if (rs.next()) { alue = new Alue(rs.getInt("alue_id"), rs. getString("nimi")); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return alue;
    }
    
    @Override
    public List<Alue> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue ORDER BY nimi");
        
        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) { alueet.add(new Alue(rs.getInt("alue_id"), rs.getString("nimi"))); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return alueet;
    }
    
    @Override
    public List<Alue> findAllIn(Collection<Integer> keys) throws SQLException {
        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) { muuttujat.append(", ?"); }
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE alue_id in ("+muuttujat+")");
        int i = 1; for (Integer key : keys) { stmt.setInt(i, key); i++; }
        
        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) { alueet.add(new Alue(rs.getInt("alue_id"), rs.getString("nimi"))); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return alueet;
    }
    
    public Alue findWithName(String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE nimi = ?");
        stmt.setString(1, nimi);
        
        ResultSet rs = stmt.executeQuery();
        Alue alue = null;
        if (rs.next()) { alue = new Alue(rs.getInt("alue_id"), rs.getString("nimi")); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return alue;
    }
    
    public List<Osiotieto> FindAllInfo() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT a.alue_id, a.nimi, MAX(v.aikaleima) as viimeisinviesti, COUNT(v.viesti_id) as viesteja FROM Alue a LEFT JOIN Viestiketju vk ON a.alue_id = vk.alue LEFT JOIN Viesti v ON vk.viestiketju_id = v.viestiketju GROUP BY a.alue_id ORDER BY a.nimi ASC");
        
        ResultSet rs = stmt.executeQuery();
        List<Osiotieto> tiedot = new ArrayList<>();
        while (rs.next()) {
            tiedot.add(new Osiotieto(rs.getInt("alue_id"), rs.getString("nimi"), rs.getInt("viesteja"), rs.getString("viimeisinviesti")));
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return tiedot;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(Alue alue) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Alue (nimi) VALUES (?)");
        stmt.setString(1, alue.getNimi());
        
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }
    
}
