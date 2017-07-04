package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.dto.paging.PagingUtils;
import fr.ebiz.computerdatabase.mapper.ComputerMapper;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.impl.CompanyDaoImpl;
import fr.ebiz.computerdatabase.persistence.dao.impl.ComputerDaoImpl;
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

    /**
     * Service constructor used to inject dao.
     *
     * @param computerDao The computer dao to inject
     * @param companyDao  The company dao to inject
     */
    private ComputerServiceImpl(ComputerDao computerDao, CompanyDao companyDao) {
        this.computerDao = computerDao;
        this.companyDao = companyDao;

        this.computerMapper = new ComputerMapper();
    }

    /**
     * Get the service instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the service singleton instance
     */
    public static synchronized ComputerService getInstance() {
        if (instance == null) {
            synchronized (ComputerServiceImpl.class) {
                if (instance == null) {
                    instance = new ComputerServiceImpl(ComputerDaoImpl.getInstance(), CompanyDaoImpl.getInstance());
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

        return computerDao
                .get(id)
                .map(computer -> Optional.of(computerMapper.toDto(computer)))
                .orElse(Optional.empty());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public Page<ComputerDto> getAll(String query, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pagination object is null");
        }

        if (pageable.getElements() <= 0) {
            throw new IllegalArgumentException("The number of returned elements must be > 0");
        }

        String nameQuery = query == null ? "" : StringUtils.cleanString(query);
        int numberOfComputers = computerDao.count(nameQuery);
        int totalPage = PagingUtils.countPages(pageable.getElements(), numberOfComputers);

        if (pageable.getPage() < 0 || pageable.getPage() > totalPage) {
            throw new IllegalArgumentException("Page number must be [0-" + totalPage + "]");
        }

        List<Computer> computers = computerDao.getAll(nameQuery, pageable.getElements(), pageable.getPage() * pageable.getElements());

        return Page.builder()
                .currentPage(pageable.getPage())
                .totalPages(totalPage)
                .totalElements(numberOfComputers)
                .elements(computerMapper.toDto(computers))
                .build();
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

        new ComputerValidator(companyDao).validate(dto);

        computerDao.insert(computerMapper.toEntity(dto));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        assertComputerIdIsNotNullAndExists(dto);

        new ComputerValidator(companyDao).validate(dto);

        computerDao.update(computerMapper.toEntity(dto));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        assertComputerIdIsNotNullAndExists(dto);

        computerDao.delete(dto.getId());
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
