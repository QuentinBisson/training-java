package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.model.Computer;

import java.util.List;
import java.util.Optional;

public interface ComputerDao {
    /**
     * Get the computer from the database.
     *
     * @param id The id of the computer to get
     * @return The computer if it exists or Optional.empty() if it does not
     */
    Optional<Computer> get(int id);

    /**
     * Get the computers from the database paginated.
     *
     * @param nameQuery name to look for
     * @param order     The order to get the elements from
     * @param elements  The number of computers to get
     * @param offset    The number of computers to skip in the results
     * @return The paginated computers
     */
    List<Computer> getAll(String nameQuery, OrderType order, int elements, int offset);

    /**
     * Count the number of elements in the database.
     *
     * @param nameQuery name to look for
     * @return the total number of elements
     */
    int count(String nameQuery);

    /**
     * Insert a computer in the database.
     *
     * @param model The computer to insert
     * @return true if the computer was inserted
     */
    boolean insert(Computer model);

    /**
     * Update a computer in the database.
     *
     * @param model The computer to update
     * @return true if the computer was updated
     */
    boolean update(Computer model);

    /**
     * Delete a computer from the database.
     *
     * @param id The computer's id to delete
     * @return true if the computer was deleted
     */
    boolean delete(Integer id);

    enum OrderType {
        NAME("computerName"), INTRODUCED("computer.introduced, computerName"), DISCONTINUED("computer.discontinued, computerName"), COMPANY("companyName, computerName");

        String field;

        /**
         * Constructor.
         *
         * @param field The field to use in DB queries
         */
        OrderType(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }
}
