
package foorumi.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface Dao<T,K> {
    
    T findOne(K key) throws SQLException;
    
    List<T> findAll() throws SQLException;
    
    List<T> findAllIn(Collection<K> keys) throws SQLException;
    
    void create(T object) throws SQLException;
    
    void delete(K key) throws SQLException;
    
}
