package fr.ebiz.computerdatabase.persistence;

import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {
    Optional<T> get(ID id);

    List<T> getAll();

    int count();

    void insert(T model);

    void update(T model);

    void delete(ID id);

}
