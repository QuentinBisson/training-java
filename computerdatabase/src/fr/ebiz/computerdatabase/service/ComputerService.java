package fr.ebiz.computerdatabase.service;

import fr.ebiz.computerdatabase.model.Computer;

import java.util.List;

public interface ComputerService {

    Computer get(int id);

    List<Computer> getAll();

    List<Computer> getAllComputersWithCompanies();

    void insert(Computer computer);
    void update(Computer computer);
    void delete(Computer computer);
}
