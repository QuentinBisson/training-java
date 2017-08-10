package fr.ebiz.computerdatabase.persistence.dao;

import com.mysema.query.types.expr.ComparableExpressionBase;
import fr.ebiz.computerdatabase.core.Computer;
import fr.ebiz.computerdatabase.core.QComputer;
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
     */
    void insert(Computer model);

    /**
     * Update a computer in the database.
     *
     * @param model The computer to update
     */
    void update(Computer model);

    /**
     * Delete a computer from the database.
     *
     * @param id The computer's id to delete
     */
    void delete(Integer id);

    /**
     * Delete computers introduced by a company.
     *
     * @param companyId The id of the company
     */
    void deleteByCompanyId(int companyId);

    /**
     * Delete a list of computer from the database.
     *
     * @param ids The computer's ids to delete
     */
    void deleteComputers(List<Integer> ids);

    enum SortColumn {
        NAME(QComputer.computer.name), INTRODUCED(QComputer.computer.introduced), DISCONTINUED(QComputer.computer.discontinued), COMPANY(QComputer.computer.company.name);

        ComparableExpressionBase<?> field;

        /**
         * Constructor.
         *
         * @param field The field to use in DB queries
         */
        SortColumn(ComparableExpressionBase<?> field) {
            this.field = field;
        }

        public ComparableExpressionBase<?> getField() {
            return field;
        }
    }
}
