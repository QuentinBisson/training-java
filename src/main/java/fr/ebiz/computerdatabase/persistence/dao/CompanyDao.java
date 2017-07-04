package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {

    /**
     * Get the company from the database.
     *
     * @param id The id of the company to get
     * @return The company if it exists or Optional.empty() if it does not
     */
    Optional<Company> get(int id);

    /**
     * Get the companies from the database paginated.
     *
     * @param elements The number of companies to get
     * @param offset   The number of companies to skip in the results
     * @return The paginated companies
     */
    List<Company> getAll(int elements, int offset);

    /**
     * Count the number of elements in the database.
     *
     * @return the total number of elements
     */
    int count();
}
