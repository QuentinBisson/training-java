package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.DaoUtils;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.transaction.impl.TransactionManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyDaoImpl implements CompanyDao {

    private static final String READ_QUERY = "SELECT * from company order by name LIMIT ? OFFSET ?";
    private static final String READ_BY_ID_QUERY = "SELECT * from company where id = ?";
    private static final String DELETE_QUERY = "DELETE FROM company WHERE id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) from company";

    private static final int FIRST_PARAMETER_INDEX = 1;
    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDaoImpl.class.getName());

    /**
     * Get the dao instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the dao singleton instance
     */
    public static CompanyDao getInstance() {
        return Dao.INSTANCE.getDao();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> get(int id) {
        try (PreparedStatement statement = TransactionManagerImpl.getInstance().getConnection().prepareStatement(READ_BY_ID_QUERY)) {

            statement.setInt(FIRST_PARAMETER_INDEX, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Company> found = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    found.add(mapEntity(resultSet));
                }

                return DaoUtils.checkOnlyOne(found, LOGGER);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> getAll(int elements, int offset) throws DaoException {
        try (PreparedStatement statement = TransactionManagerImpl.getInstance().getConnection().prepareStatement(READ_QUERY)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            statement.setInt(parameterIndex++, elements);
            statement.setInt(parameterIndex, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Company> found = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    found.add(mapEntity(resultSet));
                }
                return found;
            }

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() throws DaoException {
        try (PreparedStatement statement = TransactionManagerImpl.getInstance().getConnection().prepareStatement(COUNT_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            return resultSet != null && resultSet.next() ? resultSet.getInt(FIRST_PARAMETER_INDEX) : 0;

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * .
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Integer id) throws DaoException {
        return DaoUtils.deleteById(DELETE_QUERY, id, LOGGER);
    }

    /**
     * Map a {@link ResultSet} to a {@link Company} entity.
     *
     * @param resultSet The result set to extract data from
     * @return The mapped entity
     * @throws SQLException if an error occurs when accessing the properties from the result set
     */
    private Company mapEntity(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            return Company.builder()
                    .id(resultSet.getInt(ID_COLUMN_NAME))
                    .name(resultSet.getString(NAME_COLUMN_NAME)).build();
        }

        return null;
    }

    enum Dao {
        INSTANCE(new CompanyDaoImpl());
        private final CompanyDao dao;

        /**
         * Constructor.
         *
         * @param dao The unique company dao
         */
        Dao(CompanyDao dao) {
            this.dao = dao;
        }

        public CompanyDao getDao() {
            return dao;
        }
    }

}