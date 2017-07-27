package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.dto.paging.PagingUtils;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.ConnectionPool;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.impl.CompanyDaoImpl;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import fr.ebiz.computerdatabase.persistence.transaction.impl.TransactionManagerImpl;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CompanyServiceImpl implements CompanyService {

    private static CompanyService instance;
    private final CompanyDao companyDao;
    private final ComputerService computerService;
    private final TransactionManager transactionManager;
    private final Cache<Pageable, Page<Company>> cache;

    /**
     * Service constructor used to inject a {@link ConnectionPool} instance.
     *
     * @param companyDao         The company daoMap<String, String>
     * @param computerService    The computer dao to inject
     * @param transactionManager The transaction manager
     * @param cache              The company list cache
     */
    private CompanyServiceImpl(CompanyDao companyDao, ComputerService computerService, TransactionManager transactionManager, Cache<Pageable, Page<Company>> cache) {
        this.companyDao = companyDao;
        this.computerService = computerService;
        this.transactionManager = transactionManager;
        this.cache = cache;
    }

    /**
     * Get the service instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the service singleton instance
     */
    public static CompanyService getInstance() {
        if (instance == null) {
            synchronized (CompanyServiceImpl.class) {
                if (instance == null) {

                    MutableConfiguration<Pageable, Page<Company>> getAllConfig = new MutableConfiguration<>();
                    getAllConfig.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_HOUR));

                    CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
                    instance = new CompanyServiceImpl(CompanyDaoImpl.getInstance(), ComputerServiceImpl.getInstance(),
                            TransactionManagerImpl.getInstance(),
                            cacheManager.createCache("getAllCompanies", getAllConfig));
                }
            }
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> get(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be > 0");
        }

        transactionManager.open(false);

        try {
            return companyDao.get(id);
        } finally {
            transactionManager.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public Page<Company> getAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pagination object is null");
        }

        if (pageable.getElements() <= 0) {
            throw new IllegalArgumentException("The number of returned elements must be > 0");
        }

        if (cache.containsKey(pageable)) {
            return cache.get(pageable);
        }

        try {
            transactionManager.open(true);
            Integer numberOfCompanies = companyDao.count();

            List<Company> companies;
            Integer totalPage = PagingUtils.countPages(pageable.getElements(), numberOfCompanies);

            if (totalPage == 0) {
                companies = Collections.emptyList();
            } else {
                companies = companyDao.getAll(pageable.getElements(), pageable.getPage() * pageable.getElements());
            }

            transactionManager.commit();

            Page<Company> page = Page.builder()
                    .currentPage(pageable.getPage())
                    .totalPages(totalPage)
                    .totalElements(numberOfCompanies)
                    .elements(companies)
                    .build();

            cache.put(pageable, page);
            return page;
        } finally {
            transactionManager.close();
        }
    }

    @Override
    public void delete(Company company) {
        transactionManager.open(true);

        try {
            computerService.deleteByCompanyId(company.getId());
            companyDao.delete(company.getId());
            clearCache();
            transactionManager.commit();
        } finally {
            transactionManager.close();
        }
    }

    /**
     * Clear all the cache.
     */
    private void clearCache() {
        cache.clear();
    }

}
