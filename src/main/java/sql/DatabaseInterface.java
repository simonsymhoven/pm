package sql;

import java.util.List;
public interface DatabaseInterface<T> {
    List<T> getAll();

    T get(int id);

    T get(String symbol);

    boolean add(T t);

    boolean update(T t);

    boolean updateAll();

    boolean delete(T t);
}
