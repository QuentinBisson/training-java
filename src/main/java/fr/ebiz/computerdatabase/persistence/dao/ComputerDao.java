package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.SortOrder;

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
     * @param query    The query to search computer of company name
     * @param pageSize The number of elements per page
     * @param offset   The paging offset
     * @param column   The column to sort with
     * @param order    the sort order
     * @return The paginated computers
     */
    List<Computer> getAll(String query, int pageSize, int offset, SortColumn column, SortOrder order);

    /**
     * Count the number of elements in the database.
     *
     * @param query name to look for
     * @return the total number of elements
     */
    int count(String query);

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

    /**
     * Delete computers introduced by a company.
     *
     * @param companyId The id of the company
     * @return true if any computer was deleted
     */
    boolean deleteByCompanyId(int companyId);

    /**
     * Delete a list of computer from the database.
     *
     * @param ids The computer's ids to delete
     * @return true if the computers were deleted
     */
    boolean deleteComputers(List<Integer> ids);

    enum SortColumn {
        NAME("computerName"), INTRODUCED("introduced, computerName"), DISCONTINUED("discontinued, computerName"), COMPANY("companyName, computerName");

        String field;

        /**
         * Constructor.
         *
         * @param field The field to use in DB queries
         */
        SortColumn(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }
}
