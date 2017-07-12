package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.PagingUtils;
import fr.ebiz.computerdatabase.mapper.ComputerMapper;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.GetAllComputersRequest;
import fr.ebiz.computerdatabase.persistence.dao.impl.CompanyDaoImpl;
import fr.ebiz.computerdatabase.persistence.dao.impl.ComputerDaoImpl;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import fr.ebiz.computerdatabase.persistence.transaction.impl.TransactionManagerImpl;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.validator.impl.ComputerValidator;
import fr.ebiz.computerdatabase.utils.StringUtils;

import java.util.List;
import java.util.Optional;

public class ComputerServiceImpl implements ComputerService {

    private static ComputerService instance;
    private final ComputerDao computerDao;
    private final CompanyDao companyDao;
    private final ComputerMapper computerMapper;
    private final TransactionManager transactionManager;

    /**
     * Service constructor used to inject dao.
     *
     * @param computerDao The computer dao to inject
     * @param companyDao  The company dao to inject
     *                    @param transactionManager The transaction manager
     */
    private ComputerServiceImpl(ComputerDao computerDao, CompanyDao companyDao, TransactionManager transactionManager) {
        this.computerDao = computerDao;
        this.companyDao = companyDao;
        this.transactionManager = transactionManager;

        this.computerMapper = new ComputerMapper();
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
                    instance = new ComputerServiceImpl(ComputerDaoImpl.getInstance(), CompanyDaoImpl.getInstance(), TransactionManagerImpl.getInstance());
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
            return computerDao
                    .get(id)
                    .map(computer -> Optional.of(computerMapper.toDto(computer)))
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
    public Page<ComputerDto> getAll(GetAllComputersRequest allComputersRequest) {
        if (allComputersRequest == null) {
            throw new IllegalArgumentException("Pagination object is null");
        }

        if (allComputersRequest.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page size must be > 0");
        }

        String query = StringUtils.cleanString(allComputersRequest.getQuery());

        transactionManager.open(false);

        try {
            int numberOfComputers = computerDao.count(query);
            int totalPage = PagingUtils.countPages(allComputersRequest.getPageSize(), numberOfComputers);

            if (allComputersRequest.getPage() < 0 || allComputersRequest.getPage() > totalPage) {
                throw new IllegalArgumentException("Page number must be [0-" + totalPage + "]");
            }

            List<Computer> computers = computerDao.getAll(allComputersRequest);

            return Page.builder()
                    .currentPage(allComputersRequest.getPage())
                    .totalPages(totalPage)
                    .totalElements(numberOfComputers)
                    .elements(computerMapper.toDto(computers))
                    .build();

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

        new ComputerValidator(companyDao).validate(dto);

        try {
            computerDao.insert(computerMapper.toEntity(dto));
            transactionManager.commit();
        } finally {
            transactionManager.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ComputerDto dto) {
        transactionManager.open(true);

        assertComputerIsNotNull(dto);
        assertComputerIdIsNotNullAndExists(dto);
        new ComputerValidator(companyDao).validate(dto);

        try {
            computerDao.update(computerMapper.toEntity(dto));
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
        if (computer.getId() == null || !computerDao.get(computer.getId()).isPresent()) {
            throw new IllegalArgumentException("Computer should not have an id");
        }
    }

}
