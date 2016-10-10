
package foorumi.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Dao<T,K> {
    
    T findOne(K key) throws SQLException;
    
    List<T> findAll() throws SQLException;
    
    List<T> findAllIn(Collection<K> keys) throws SQLException;
    
    List<T> findAllWithValue(String attribute, String value) throws SQLException;
    
    void delete(K key) throws SQLException;
    
    void save(T object) throws SQLException;
    
}