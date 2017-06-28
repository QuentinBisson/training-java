package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {

    Optional<Company> get(int id);

    List<Company> getAll(int elements, int offset);

    int count();
}
