
package foorumi.database;

import foorumi.domain.Alue;
import foorumi.domain.Viestiketju;
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

public class ViestiketjuDao implements Dao<Viestiketju, Integer> {
    
    private Database database;
    private AlueDao alueDao;

    public ViestiketjuDao(Database database, AlueDao alueDao) {
        this.database = database;
        this.alueDao = alueDao;
    }

    @Override
    public Viestiketju findOne(Integer key) throws SQLException {
        List<Viestiketju> viestiketjut = listQuery("WHERE viestiketju_id = " + key);
        
        if (viestiketjut.isEmpty()) { return null; }
        return viestiketjut.get(0);
    }

    @Override
    public List<Viestiketju> findAll() throws SQLException {
        return listQuery("");
    }
    
    @Override
    public List<Viestiketju> findAllIn(Collection<Integer> keys) throws SQLException {
        StringBuilder sb = new StringBuilder();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) { sb.append(", "); }
        }
        return listQuery("WHERE viestiketju_id IN (" + sb.toString() + ")");
    }
    
    @Override
    public List<Viestiketju> findAllWithValue(String attribute, String value) throws SQLException {
        return listQuery("WHERE " + attribute + " = '" + value + "'");
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<Viestiketju> listQuery(String postfix) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju " + postfix);
        
        ResultSet rs = stmt.executeQuery();
        
        Map<Integer, List<Viestiketju>> viestiketjuAlueet = new HashMap<>();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        while (rs.next()) {
                int alue = rs.getInt("alue");
                Viestiketju viestiketju = new Viestiketju(rs.getInt("viestiketju_id"), rs.getString("otsikko"));
                viestiketjut.add(viestiketju);
                if (!viestiketjuAlueet.containsKey(alue)) { viestiketjuAlueet.put(alue, new ArrayList<>()); }
                viestiketjuAlueet.get(alue).add(viestiketju);
        }
        
        for (Alue alue : alueDao.findAllIn(viestiketjuAlueet.keySet())) {
            for (Viestiketju viestiketju : viestiketjuAlueet.get(alue.getAlue_id())) {
                viestiketju.setAlue(alue);
            }
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return viestiketjut;
    }

    @Override
    public Viestiketju findOneWithValue(String attribute, String value) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int save(String... args) throws SQLException {
        if (args.length != 2) { return 0; }
        
        Alue alue = alueDao.findOne(Integer.parseInt(args[0]));
        if (alue == null) { return 0; }
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viestiketju (alue, otsikko) VALUES (?,?)");
        stmt.setInt(1, alue.getAlue_id());
        stmt.setString(2, args[1]);
        
        int muutokset = stmt.executeUpdate();
        
        stmt.close();
        connection.close();
        
        return muutokset;
    }
    
}