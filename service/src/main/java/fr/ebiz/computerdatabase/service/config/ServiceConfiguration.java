package fr.ebiz.computerdatabase.service.config;

import fr.ebiz.computerdatabase.persistence.config.RepositoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
// Enable component scan on repository and services
@ComponentScan({
        "fr.ebiz.computerdatabase.service",
        "fr.ebiz.computerdatabase.mapper"
})
@EnableTransactionManagement // Enable transactions
@Import(RepositoryConfiguration.class)
public class ServiceConfiguration {


    private final RepositoryConfiguration config;

    /**
     * Constructor.
     *
     * @param config The repository configuration
     */
    @Autowired
    public ServiceConfiguration(RepositoryConfiguration config) {
        this.config = config;
    }

    /**
     * Create the Transaction manager.
     *
     * @param entityManagerFactory the entity manager factory
     * @return The created datasource
     */
    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setDataSource(config.dataSource());
        transactionManager.setJpaDialect(new HibernateJpaDialect());
        return transactionManager;
    }


}
