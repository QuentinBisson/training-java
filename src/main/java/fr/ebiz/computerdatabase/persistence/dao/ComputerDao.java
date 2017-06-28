package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.model.Computer;

import java.util.List;
import java.util.Optional;

public interface ComputerDao {

    Optional<Computer> get(int id);

    List<Computer> getAll(int elements, int offset);

    int count();

    boolean insert(Computer model);

    boolean update(Computer model);

    boolean delete(Integer id);
}
