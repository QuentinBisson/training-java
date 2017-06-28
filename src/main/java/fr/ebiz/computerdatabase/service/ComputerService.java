package fr.ebiz.computerdatabase.service;

import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.service.impl.paging.Page;
import fr.ebiz.computerdatabase.service.impl.paging.Pageable;

import java.util.Optional;

public interface ComputerService {

    Optional<Computer> get(int id);

    Page<Computer> getAll(Pageable pageable);

    void insert(Computer computer);

    void update(Computer computer);

    void delete(Computer computer);


}
