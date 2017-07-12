package fr.ebiz.computerdatabase.persistence.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.ebiz.computerdatabase.persistence.exception.DaoConfigurationException;
import fr.ebiz.computerdatabase.utils.PropertiesUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class ConnectionPool {

    private static final String PREP_STMT_CACHE_SIZE_PROP = "prepStmtCacheSize";
    private static final String CONFIGURATION_FILE_PROPERTY = "db.properties";
    private static final String CACHE_PREP_STMTS_PROP = "cachePrepStmts";
    private static final String PREP_STMT_CACHE_SQL_LIMIT_PROP = "prepStmtCacheSqlLimit";
    private static final String USE_SERVER_PREP_STMTS_PROP = "useServerPrepStmts";
    private static final String USE_LOCAL_SESSION_STATE_PROP = "useLocalSessionState";
    private static final String USE_LOCAL_TRANSACTION_STATE_PROP = "useLocalTransactionState";
    private static final String REWRITE_BATCHED_STATEMENTS_PROP = "rewriteBatchedStatements";
    private static final String CACHE_RESULT_SET_METADATA_PROP = "cacheResultSetMetadata";
    private static final String CACHE_SERVER_CONFIGURATION_PROP = "cacheServerConfiguration";

    public static DataSource getInstance() {
        return Pool.INSTANCE.getDataSource();
    }

    /**
     * Create a HikariCP DataSource.
     *
     * @param propertyFile The property file
     * @return The datasource
     */
    private static HikariDataSource newDataSource(String propertyFile) {
        Properties properties;
        try {
            properties = PropertiesUtils.loadProperties(propertyFile);
        } catch (IOException e) {
            throw new DaoConfigurationException(e.getMessage(), e);
        }

        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName(properties.getProperty("poolName"));
        jdbcConfig.setMaximumPoolSize(PropertiesUtils.getIntProperty(properties, "maximumPoolSize"));
        jdbcConfig.setMinimumIdle(PropertiesUtils.getIntProperty(properties, "minimumIdle"));
        jdbcConfig.setJdbcUrl(properties.getProperty("url"));
        jdbcConfig.setDriverClassName(properties.getProperty("driverClassName"));
        jdbcConfig.setUsername(properties.getProperty("username"));
        jdbcConfig.setPassword(properties.getProperty("password"));
        jdbcConfig.setIdleTimeout(PropertiesUtils.getIntProperty(properties, "idleTimeout"));
        jdbcConfig.setInitializationFailTimeout(1);
        jdbcConfig.setLeakDetectionThreshold(10);

        jdbcConfig.addDataSourceProperty(CACHE_PREP_STMTS_PROP, PropertiesUtils.getBooleanProperty(properties, CACHE_PREP_STMTS_PROP));
        jdbcConfig.addDataSourceProperty(PREP_STMT_CACHE_SIZE_PROP, PropertiesUtils.getIntProperty(properties, PREP_STMT_CACHE_SIZE_PROP));
        jdbcConfig.addDataSourceProperty(PREP_STMT_CACHE_SQL_LIMIT_PROP, PropertiesUtils.getIntProperty(properties, PREP_STMT_CACHE_SQL_LIMIT_PROP));
        jdbcConfig.addDataSourceProperty(USE_SERVER_PREP_STMTS_PROP, PropertiesUtils.getBooleanProperty(properties, USE_SERVER_PREP_STMTS_PROP));
        jdbcConfig.addDataSourceProperty(USE_LOCAL_SESSION_STATE_PROP, PropertiesUtils.getBooleanProperty(properties, USE_LOCAL_SESSION_STATE_PROP));
        jdbcConfig.addDataSourceProperty(USE_LOCAL_TRANSACTION_STATE_PROP, PropertiesUtils.getBooleanProperty(properties, USE_LOCAL_TRANSACTION_STATE_PROP));
        jdbcConfig.addDataSourceProperty(REWRITE_BATCHED_STATEMENTS_PROP, PropertiesUtils.getBooleanProperty(properties, REWRITE_BATCHED_STATEMENTS_PROP));
        jdbcConfig.addDataSourceProperty(CACHE_RESULT_SET_METADATA_PROP, PropertiesUtils.getBooleanProperty(properties, CACHE_RESULT_SET_METADATA_PROP));
        jdbcConfig.addDataSourceProperty(CACHE_SERVER_CONFIGURATION_PROP, PropertiesUtils.getBooleanProperty(properties, CACHE_SERVER_CONFIGURATION_PROP));

        return new HikariDataSource(jdbcConfig);
    }

    /*
     *  This pool is made for short quick transactions that the web application uses.
     *  Using enum singleton pattern for lazy singletons
     */
    private enum Pool {
        INSTANCE(newDataSource(CONFIGURATION_FILE_PROPERTY));
        private final DataSource dataSource;

        /**
         * Constructor.
         *
         * @param dataSource The unique datasource
         */
        Pool(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public DataSource getDataSource() {
            return dataSource;
        }
    }

}
