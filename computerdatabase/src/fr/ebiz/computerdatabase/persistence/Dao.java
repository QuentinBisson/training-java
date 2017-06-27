package fr.ebiz.computerdatabase.persistence;

import fr.ebiz.computerdatabase.persistence.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {
    Optional<T> get(ID id) throws DaoException;

    List<T> getAll() throws DaoException;

    int count() throws DaoException;

    void insert(T model) throws DaoException;

    void update(T model) throws DaoException;

    void delete(ID id) throws DaoException;

}
