package fr.ebiz.computerdatabase.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

@Configuration
// Enable component scan on repository and services
@ComponentScan({
        "fr.ebiz.computerdatabase.persistence",
        "fr.ebiz.computerdatabase.service",
        "fr.ebiz.computerdatabase.mapper"
})
@EnableTransactionManagement // Enable transactions
@PropertySource("classpath:db.properties")
public class ServiceConfiguration implements TransactionManagementConfigurer {

    @Value("${poolName}")
    private String poolName;
    @Value("${url}")
    private String jdbcUrl;
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;
    @Value("${driverClassName}")
    private String driver;
    @Value("${maximumPoolSize}")
    private int maximumPoolSize;
    @Value("${idleTimeout}")
    private int idleTimeout;

    /**
     * Create the property placeholder with the properties configured in the @PropertySource annotation.
     *
     * @return The created {@link PropertyPlaceholderConfigurer}
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Create the DataSource bean.
     *
     * @return The created datasource
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName(poolName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setIdleTimeout(idleTimeout);
        return new HikariDataSource(config);
    }

    /**
     * Create the Transaction manager.
     *
     * @return The created datasource
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
