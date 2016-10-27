
package foorumi.database;

import foorumi.domain.Alue;
import foorumi.domain.Osiotieto;
import foorumi.domain.Viestiketju;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE viestiketju_id = ?");
        stmt.setInt(1, key);
        
        ResultSet rs = stmt.executeQuery();
        Viestiketju viestiketju = null;
        if (rs.next()) { viestiketju = new Viestiketju(rs.getInt("viestiketju_id"), alueDao.findOne(rs.getInt("alue")), rs.getString("otsikko")); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return viestiketju;
    }
    
    @Override
    public List<Viestiketju> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Viestiketju> findAllIn(Collection<Integer> keys) throws SQLException {
        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE viestiketju_id IN (" + muuttujat + ")");
        int i = 1; for (Integer key : keys) { stmt.setInt(i, key); i++; }
        
        ResultSet rs = stmt.executeQuery();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        Map<Integer, List<Viestiketju>> viestiketjutAlueenMukaan = new HashMap<>();
        while (rs.next()) {
            Viestiketju viestiketju = new Viestiketju(rs.getInt("viestiketju_id"), null, rs.getString("otsikko"));
            viestiketjut.add(viestiketju);
            int alue = rs.getInt("alue");
            if (!viestiketjutAlueenMukaan.containsKey(alue)) { viestiketjutAlueenMukaan.put(alue, new ArrayList<>()); }
            viestiketjutAlueenMukaan.get(alue).add(viestiketju);
        }
        
        for (Alue alue : alueDao.findAllIn(viestiketjutAlueenMukaan.keySet())) {
            for (Viestiketju viestiketju : viestiketjutAlueenMukaan.get(alue.getAlue_id())) {
                viestiketju.setAlue(alue);
            }
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return viestiketjut;
    }
    
    public Viestiketju findWithOtsikko(String otsikko) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE otsikko = ?");
        stmt.setString(1, otsikko);
        
        ResultSet rs = stmt.executeQuery();
        Viestiketju viestiketju = null;
        if (rs.next()) { viestiketju = new Viestiketju(rs.getInt("viestiketju_id"), alueDao.findOne(rs.getInt("alue")), rs.getString("otsikko")); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return viestiketju;
    }
    
    public List<Viestiketju> findAllWithAlue(Alue alue) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE alue = ?");
        stmt.setInt(1, alue.getAlue_id());
        
        ResultSet rs = stmt.executeQuery();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        while(rs.next()) { viestiketjut.add(new Viestiketju(rs.getInt("viestiketju_id"), alue, rs.getString("otsikko"))); }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return viestiketjut;
    }
    
    public List<Viestiketju> findAllWithAlueIn(Collection<Alue> alueet) throws SQLException {
        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < alueet.size(); i++) { muuttujat.append(", ?"); }
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE alue IN (" + muuttujat + ")");
        int i = 1; for (Alue alue : alueet) { stmt.setInt(i, alue.getAlue_id()); i++; }
        
        ResultSet rs = stmt.executeQuery();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        Map<Integer, List<Viestiketju>> viestiketjutAlueenMukaan = new HashMap<>();
        while (rs.next()) {
            Viestiketju viestiketju = new Viestiketju(rs.getInt("viestiketju_id"), null, rs.getString("otsikko"));
            viestiketjut.add(viestiketju);
            int alue = rs.getInt("alue");
            if (!viestiketjutAlueenMukaan.containsKey(alue)) { viestiketjutAlueenMukaan.put(alue, new ArrayList<>()); }
            viestiketjutAlueenMukaan.get(alue).add(viestiketju);
        }
        
        for (Alue alue : alueet) {
            for (Viestiketju viestiketju : viestiketjutAlueenMukaan.get(alue.getAlue_id())) {
                viestiketju.setAlue(alue);
            }
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return viestiketjut;
    }
    
    public List<Osiotieto> findInfoFromAlue(Alue alue, int mista, int monta) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT vk.viestiketju_id, vk.otsikko, MAX(v.aikaleima) AS viimeisinviesti, COUNT(v.viesti_id) AS viesteja FROM Viestiketju vk LEFT JOIN Viesti v ON vk.viestiketju_id = v.viestiketju WHERE vk.alue = ? GROUP BY vk.viestiketju_id ORDER BY viimeisinviesti DESC LIMIT ? OFFSET ?");
        stmt.setInt(1, alue.getAlue_id());
        stmt.setInt(2, monta);
        stmt.setInt(3, mista);
        
        ResultSet rs = stmt.executeQuery();
        
        List<Osiotieto> tiedot = new ArrayList<>();
        while (rs.next()) {
            tiedot.add(new Osiotieto(rs.getInt("viestiketju_id"), rs.getString("otsikko"), rs.getInt("viesteja"), rs.getString("viimeisinviesti")));
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return tiedot;
    }

    @Override
    public void create(Viestiketju viestiketju) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viestiketju (alue, otsikko) VALUES (?, ?)");
        stmt.setInt(1, viestiketju.getAlue().getAlue_id());
        stmt.setString(2, viestiketju.getOtsikko());
        
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
