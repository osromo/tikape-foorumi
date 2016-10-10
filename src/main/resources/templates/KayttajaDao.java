
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
        List<Kayttaja> kayttajat = listQuery("WHERE kayttaja_id = " + key);
        
        if (kayttajat.isEmpty()) { return null; }
        return kayttajat.get(0);
    }

    @Override
    public List<Kayttaja> findAll() throws SQLException {
        return listQuery("");
    }
    
    @Override
    public List<Kayttaja> findAllIn(Collection<Integer> keys) throws SQLException {
        StringBuilder sb = new StringBuilder();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) { sb.append(", "); }
        }
        return listQuery("WHERE kayttaja_id IN (" + sb.toString() + ")");
    }
    
    @Override
    public List<Kayttaja> findAllWithValue(String attribute, String value) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<Kayttaja> listQuery(String postfix) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja " + postfix);
        ResultSet rs = stmt.executeQuery();
        
        List<Kayttaja> kayttajat = new ArrayList<>();
        while (rs.next()) {
            kayttajat.add(new Kayttaja(rs.getInt("kayttaja_id"), rs.getString("nimimerkki")));
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return kayttajat;
    }

    @Override
    public Kayttaja findOneWithValue(String attribute, String value) throws SQLException {
        List<Kayttaja> kayttajat = listQuery("WHERE " + attribute + " = '" + value + "'");
        
        if (kayttajat.isEmpty()) { return null; }
        return kayttajat.get(0);
    }

    @Override
    public int save(String... args) throws SQLException {
        if (args.length != 1) { return 0; }

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Kayttaja (nimimerkki) VALUES (?)");
        stmt.setString(1, args[0]);

        int muutokset = stmt.executeUpdate();

        stmt.close();
        connection.close();

        return muutokset;
    }
    
}
