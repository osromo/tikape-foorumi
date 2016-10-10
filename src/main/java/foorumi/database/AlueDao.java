
package foorumi.database;

import foorumi.domain.Alue;
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
        List<Alue> alueet = listQuery("WHERE alue_id = " + key);
        
        if (alueet.isEmpty()) { return null; }
        return alueet.get(0);
    }
    
    @Override
    public List<Alue> findAll() throws SQLException {
        return listQuery("");
    }
    
    @Override
    public List<Alue> findAllIn(Collection<Integer> keys) throws SQLException {
        StringBuilder sb = new StringBuilder();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) { sb.append(", "); }
        }
        return listQuery("WHERE alue_id IN (" + sb.toString() + ")");
    }
    
    @Override
    public List<Alue> findAllWithValue(String attribute, String value) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<Alue> listQuery(String postfix) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue " + postfix);
        ResultSet rs = stmt.executeQuery();
        
        List<Alue> alueet = new ArrayList<>();
        while(rs.next()) {
            alueet.add(new Alue(rs.getInt("alue_id"), rs.getString("nimi")));
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return alueet;
    }

    @Override
    public void save(Alue alue) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Alue (nimi) VALUES (?)");
        stmt.setObject(1, alue.getNimi());
        
        int changes = stmt.executeUpdate();
        System.out.println(changes);
        
        stmt.close();
        connection.close();
    }
    
}
