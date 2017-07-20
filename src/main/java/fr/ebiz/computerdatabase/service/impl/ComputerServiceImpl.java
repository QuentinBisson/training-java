package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.PagingUtils;
import fr.ebiz.computerdatabase.mapper.ComputerMapper;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.GetAllComputersRequest;
import fr.ebiz.computerdatabase.persistence.dao.impl.ComputerDaoImpl;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import fr.ebiz.computerdatabase.persistence.transaction.impl.TransactionManagerImpl;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.validator.impl.ComputerValidator;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ComputerServiceImpl implements ComputerService {

    private static final String GET_ALL_COMPUTERS_CACHE = "getAllComputers";
    private static ComputerService instance;

    private final ComputerDao computerDao;
    private final TransactionManager transactionManager;
    private final Cache<GetAllComputersRequest, Page<ComputerDto>> cache;

    /**
     * Service constructor used to inject dao.
     *
     * @param computerDao        The computer dao to inject
     * @param transactionManager The transaction manager
     * @param cache              The computer cache
     */
    private ComputerServiceImpl(ComputerDao computerDao,
                                TransactionManager transactionManager,
                                Cache<GetAllComputersRequest, Page<ComputerDto>> cache) {
        this.computerDao = computerDao;
        this.transactionManager = transactionManager;
        this.cache = cache;
    }

    /**
     * Get the service instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the service singleton instance
     */
    public static ComputerService getInstance() {
        if (instance == null) {
            synchronized (ComputerServiceImpl.class) {
                if (instance == null) {
                    CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
                    MutableConfiguration<GetAllComputersRequest, Page<ComputerDto>> config = new MutableConfiguration<>();
                    config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_HOUR));

                    instance = new ComputerServiceImpl(
                            ComputerDaoImpl.getInstance(),
                            TransactionManagerImpl.getInstance(),
                            cacheManager.createCache(GET_ALL_COMPUTERS_CACHE, config));
                }
            }
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ComputerDto> get(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be > 0");
        }

        transactionManager.open(false);
        try {
            return computerDao.get(id).map(c -> Optional.of(ComputerMapper.getInstance().toDto(c)))
                    .orElse(Optional.empty());
        } finally {
            transactionManager.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public Page<ComputerDto> getAll(GetAllComputersRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Pagination object is null");
        }

        if (request.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page size must be > 0");
        }

        if (cache.containsKey(request)) {
            return cache.get(request);
        }

        try {
            transactionManager.open(true);
            Integer numberOfComputers = computerDao.count(request.getQuery());

            Integer totalPage = PagingUtils.countPages(request.getPageSize(), numberOfComputers);

            if (request.getPage() < 0 || request.getPage() > totalPage) {
                throw new IllegalArgumentException("Page number must be [0-" + totalPage + "]");
            }

            List<Computer> computers;
            if (totalPage == 0) {
                computers = Collections.emptyList();
            } else {
                computers = computerDao.getAll(request);
            }

            transactionManager.commit();
            Page<ComputerDto> page = Page.builder()
                    .currentPage(request.getPage())
                    .totalPages(totalPage)
                    .totalElements(numberOfComputers)
                    .elements(ComputerMapper.getInstance().toDto(computers))
                    .build();

            cache.put(request, page);
            return page;

        } finally {
            transactionManager.close();

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        if (dto.getId() != null) {
            throw new IllegalArgumentException("Computer should not have an id");
        }

        transactionManager.open(true);
        ComputerValidator.getInstance().validate(dto);

        try {
            computerDao.insert(ComputerMapper.getInstance().toEntity(dto));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        assertComputerIdIsNotNullAndExists(dto);

        transactionManager.open(true);
        ComputerValidator.getInstance().validate(dto);

        try {
            computerDao.update(ComputerMapper.getInstance().toEntity(dto));
            clearCache();
            transactionManager.commit();
        } finally {
            transactionManager.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(ComputerDto dto) {
        transactionManager.open(true);

        assertComputerIsNotNull(dto);
        assertComputerIdIsNotNullAndExists(dto);

        try {
            computerDao.delete(dto.getId());
            clearCache();
            transactionManager.commit();
        } finally {
            transactionManager.close();
        }
    }

    @Override
    public void deleteByCompanyId(Integer companyId) {
        transactionManager.open(true);

        try {
            computerDao.deleteByCompanyId(companyId);
            clearCache();
            transactionManager.commit();
        } finally {
            transactionManager.close();
        }
    }

    @Override
    public void deleteComputers(List<Integer> ids) {
        transactionManager.open(true);


        try {
            computerDao.deleteComputers(ids);
            clearCache();
            transactionManager.commit();
        } finally {
            transactionManager.close();
        }
    }

    /**
     * Assert the computer object is not null, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param computer The computer to test
     */
    private void assertComputerIsNotNull(ComputerDto computer) {
        if (computer == null) {
            throw new IllegalArgumentException("Computer object is null");
        }
    }

    /**
     * Assert the computer object has an id and exists in the database, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param computer The computer to test
     */
    private void assertComputerIdIsNotNullAndExists(ComputerDto computer) {
        if (computer.getId() == null || !get(computer.getId()).isPresent()) {
            throw new IllegalArgumentException("Computer should have an id and exist in the database");
        }
    }

}
