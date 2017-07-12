package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.DaoUtils;
import fr.ebiz.computerdatabase.persistence.dao.GetAllComputersRequest;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import fr.ebiz.computerdatabase.persistence.transaction.impl.TransactionManagerImpl;
import fr.ebiz.computerdatabase.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComputerDaoImpl implements ComputerDao {

    private static final String READ_QUERY = "SELECT computer.id, computer.name as computerName, computer.introduced, computer.discontinued, computer.company_id, company.name as companyName FROM computer computer LEFT JOIN company company ON computer.company_id = company.id";
    private static final String READ_BY_ID_QUERY = "SELECT computer.id, computer.name as computerName, computer.introduced, computer.discontinued, computer.company_id, company.name as companyName from computer LEFT JOIN company company ON computer.company_id = company.id where computer.id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) from computer";
    private static final String INSERT_QUERY = "INSERT INTO computer(name, introduced, discontinued, company_id) VALUES (?, ?, ?, ?) ";
    private static final String UPDATE_QUERY = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM computer WHERE id = ?";
    private static final String DELETE_COMPUTERS_FOR_COMPANY_QUERY = "DELETE FROM computer WHERE company_id = ?";

    private static final int FIRST_PARAMETER_INDEX = 1;
    private static final String ID_COLUMN_NAME = "id";
    private static final String INTRODUCED_COLUMN_NAME = "introduced";
    private static final String DISCONTINUED_COLUMN_NAME = "discontinued";
    private static final String COMPANY_ID_COLUMN_NAME = "company_id";
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDaoImpl.class.getName());

    private static ComputerDao instance;
    private final Cache<GetAllComputersRequest, List<Computer>> allComputersCache;
    private final Cache<String, Integer> countComputersCache;

    /**
     * Constructor.
     *
     * @param allComputersCache   The computer list cache
     * @param countComputersCache The count catch
     */
    private ComputerDaoImpl(Cache<GetAllComputersRequest, List<Computer>> allComputersCache, Cache<String, Integer> countComputersCache) {
        this.allComputersCache
                = allComputersCache;
        this.countComputersCache = countComputersCache;
    }

    /**
     * Get the dao instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the dao singleton instance
     */
    public static ComputerDao getInstance() {
        if (instance == null) {
            synchronized (ComputerDaoImpl.class) {
                if (instance == null) {
                    MutableConfiguration<GetAllComputersRequest, List<Computer>> getAllConfig = new MutableConfiguration<>();
                    getAllConfig.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_HOUR));

                    MutableConfiguration<String, Integer> countConfig = new MutableConfiguration<>();
                    getAllConfig.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_HOUR));

                    CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

                    instance = new ComputerDaoImpl(cacheManager.createCache("getAllComputers", getAllConfig), cacheManager.createCache("countComputers", countConfig));
                }
            }
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Computer> get(int id) {
        try (PreparedStatement statement = TransactionManagerImpl.getInstance().getConnection().prepareStatement(READ_BY_ID_QUERY)) {

            statement.setInt(FIRST_PARAMETER_INDEX, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Computer> found = new ArrayList<>();
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
    public List<Computer> getAll(GetAllComputersRequest request) {
        if (allComputersCache.containsKey(request)) {
            return allComputersCache.get(request);
        }

        String stringQuery = READ_QUERY;
        if (!StringUtils.isBlank(request.getQuery())) {
            stringQuery += " WHERE lower(computer.name) LIKE ? OR lower(company.name) LIKE ? ";
        }
        stringQuery += String.format(" ORDER BY %s %s LIMIT ? OFFSET ? ", request.getColumn().getField(), request.getOrder().name());
        try (PreparedStatement statement = TransactionManagerImpl.getInstance().getConnection().prepareStatement(stringQuery)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            if (!StringUtils.isBlank(request.getQuery())) {
                String likeParameter = request.getQuery() + "%";
                statement.setString(parameterIndex++, likeParameter); // Computer name
                statement.setString(parameterIndex++, likeParameter); // Company name
            }

            statement.setInt(parameterIndex++, request.getPageSize());
            statement.setInt(parameterIndex, request.getOffset());

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Computer> found = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    found.add(mapEntity(resultSet));
                }
                allComputersCache.put(request, found);
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
    public int count(String query) {

        if (countComputersCache.containsKey(query)) {
            return countComputersCache.get(query);
        }

        String stringQuery = COUNT_QUERY;

        if (!StringUtils.isBlank(query)) {
            stringQuery += " LEFT JOIN company company ON computer.company_id = company.id WHERE lower(computer.name) LIKE ? OR lower(company.name) LIKE ?";
        }
        try (PreparedStatement statement = TransactionManagerImpl.getInstance().getConnection().prepareStatement(stringQuery)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            if (!StringUtils.isBlank(query)) {
                String likeParameter = query + "%";
                statement.setString(parameterIndex++, likeParameter); // Computer name
                statement.setString(parameterIndex, likeParameter); // Company name
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet != null && resultSet.next()) {
                    int total = resultSet.getInt(FIRST_PARAMETER_INDEX);
                    countComputersCache.put(query, total);
                    return total;
                }
            }

            return 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insert(final Computer computer) {
        TransactionManager tx = TransactionManagerImpl.getInstance();
        try (PreparedStatement statement = tx.getConnection().prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            bindParameters(computer, statement, FIRST_PARAMETER_INDEX);

            int affectedRows = statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                getGeneratedId(resultSet, computer);
            }
            clearCache();
            return affectedRows == 1;

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            tx.rollback();
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * Clear all the cache.
     */
    private void clearCache() {
        allComputersCache.removeAll();
        countComputersCache.removeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(final Computer computer) {
        TransactionManager tx = TransactionManagerImpl.getInstance();
        try (PreparedStatement statement = tx.getConnection().prepareStatement(UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            int parameterIndex = bindParameters(computer, statement, FIRST_PARAMETER_INDEX);
            statement.setInt(parameterIndex, computer.getId());

            int affectedRows = statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                getGeneratedId(resultSet, computer);
            }
            clearCache();
            return affectedRows == 1;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            tx.rollback();
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Integer id) {
        boolean result = DaoUtils.deleteById(DELETE_QUERY, id, LOGGER);
        clearCache();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteByCompanyId(Integer companyId) {
        TransactionManager tx = TransactionManagerImpl.getInstance();
        try (PreparedStatement statement = tx.getConnection().prepareStatement(DELETE_COMPUTERS_FOR_COMPANY_QUERY)) {
            statement.setInt(FIRST_PARAMETER_INDEX, companyId);
            int affectedRows = statement.executeUpdate();
            clearCache();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            tx.rollback();
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * Bind query parameters from the model in the prepared statement.
     *
     * @param computer       The computer to bind properties from
     * @param statement      The statement to bind
     * @param parameterIndex The parameter index
     * @return the current parameter index in case we need to add other parameters
     * @throws SQLException if an error occurs when binding values in the statement
     */
    private int bindParameters(Computer computer, PreparedStatement statement, int parameterIndex) throws SQLException {
        statement.setString(parameterIndex++, computer.getName());

        if (computer.getIntroduced() != null) {
            statement.setTimestamp(parameterIndex++, DaoUtils.toTimestamp(computer.getIntroduced()));
        } else {
            statement.setNull(parameterIndex++, Types.DATE);
        }
        if (computer.getDiscontinued() != null) {
            statement.setTimestamp(parameterIndex++, DaoUtils.toTimestamp(computer.getDiscontinued()));
        } else {
            statement.setNull(parameterIndex++, Types.DATE);
        }
        if (computer.getCompany() != null && computer.getCompany().getId() != null) {
            statement.setInt(parameterIndex++, computer.getCompany().getId());
        } else {
            statement.setNull(parameterIndex++, Types.INTEGER);
        }
        return parameterIndex;
    }

    /**
     * Map a {@link ResultSet} to a {@link Computer} entity.
     *
     * @param resultSet The result set to extract data from
     * @return The mapped entity
     * @throws SQLException if an error occurs when accessing the properties from the result set
     */
    private Computer mapEntity(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            Computer.ComputerBuilder builder = Computer.builder()
                    .id(resultSet.getInt(ID_COLUMN_NAME))
                    .name(resultSet.getString("computerName"))
                    .introduced(DaoUtils.toDate(resultSet.getTimestamp(INTRODUCED_COLUMN_NAME)))
                    .discontinued(DaoUtils.toDate(resultSet.getTimestamp(DISCONTINUED_COLUMN_NAME)));

            // Handle the possibility of having the company id as null
            Integer companyId = resultSet.getInt(COMPANY_ID_COLUMN_NAME);
            if (resultSet.wasNull()) {
                companyId = null;
            }
            builder.company(Company.builder()
                    .id(companyId)
                    .name(resultSet.getString("companyName"))
                    .build());
            return builder.build();
        }

        return null;
    }

    /**
     * Get the generated if from the result set and update the domain object.
     *
     * @param resultSet The result set to extract generated ids from
     * @param model     The model to update
     * @throws SQLException if an error occurs when accessing the properties from the result set
     */
    private void getGeneratedId(ResultSet resultSet, Computer model) throws SQLException {
        if (resultSet != null && !resultSet.isClosed() && resultSet.next()) {
            model.setId(resultSet.getInt(FIRST_PARAMETER_INDEX));
        }
    }

}
