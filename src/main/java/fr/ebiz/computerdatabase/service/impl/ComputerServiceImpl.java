package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.impl.CompanyDaoImpl;
import fr.ebiz.computerdatabase.persistence.dao.impl.ComputerDaoImpl;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.impl.paging.Page;
import fr.ebiz.computerdatabase.service.impl.paging.Pageable;
import fr.ebiz.computerdatabase.service.impl.paging.PagingUtils;

import java.util.List;
import java.util.Optional;

public class ComputerServiceImpl implements ComputerService {

    private static ComputerService instance;
    private ComputerDao computerDao;
    private CompanyDao companyDao;

    private ComputerServiceImpl(ComputerDao computerDao, CompanyDao companyDao) {
        this.computerDao = computerDao;
        this.companyDao = companyDao;
    }

    public synchronized static ComputerService getInstance() {
        if (instance == null) {
            instance = new ComputerServiceImpl(ComputerDaoImpl.getInstance(), CompanyDaoImpl.getInstance());
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Computer> get(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be > 0");
        }

        return computerDao.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public Page<Computer> getAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pagination object is null");
        }

        if (pageable.getElements() <= 0) {
            throw new IllegalArgumentException("The number of returned elements must be > 0");
        }

        int numberOfComputers = computerDao.count();
        int totalPage = PagingUtils.countPages(pageable.getElements(), numberOfComputers);

        if (pageable.getPage() < 0 || pageable.getPage() > totalPage) {
            throw new IllegalArgumentException("Page number must be [0-" + totalPage + "]");
        }

        List<Computer> computers = computerDao.getAll(pageable.getElements(), pageable.getPage() * pageable.getElements());

        return Page.builder()
                .currentPage(pageable.getPage())
                .totalPages(totalPage)
                .elements(computers)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(Computer computer) {
        assertComputerIsNotNull(computer);
        if (computer.getId() != null) {
            throw new IllegalArgumentException("Computer should not have an id");
        }

        assertComputerNameIsFilled(computer);
        assertComputerIntroductionDateIsNullOrValid(computer);
        assertComputerDiscontinuedDateIsNullOrValid(computer);
        assertCompanyIsNullOrExist(computer);

        computerDao.insert(computer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Computer computer) {
        assertComputerIsNotNull(computer);
        assertComputerIdIsNotNullAndExists(computer);
        assertComputerNameIsFilled(computer);
        assertComputerIntroductionDateIsNullOrValid(computer);
        assertComputerDiscontinuedDateIsNullOrValid(computer);

        assertCompanyIsNullOrExist(computer);

        computerDao.update(computer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Computer computer) {
        assertComputerIsNotNull(computer);
        assertComputerIdIsNotNullAndExists(computer);

        computerDao.delete(computer.getId());
    }

    private void assertComputerIsNotNull(Computer computer) {
        if (computer == null) {
            throw new IllegalArgumentException("Computer object is null");
        }
    }

    private void assertComputerIdIsNotNullAndExists(Computer computer) {
        if (computer.getId() == null || !computerDao.get(computer.getCompanyId()).isPresent()) {
            throw new IllegalArgumentException("Computer should not have an id");
        }
    }

    private void assertComputerNameIsFilled(Computer computer) {
        if (computer.getName() == null || computer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Computer name is mandatory not have an id");
        }
    }

    private void assertComputerIntroductionDateIsNullOrValid(Computer computer) {
        if (computer.getIntroduced() != null && computer.getIntroduced().toEpochSecond() < 0) {
            throw new IllegalArgumentException("Introduction date must be null or a valid timestamp");
        }
    }

    private void assertComputerDiscontinuedDateIsNullOrValid(Computer computer) {
        if (computer.getDiscontinued() != null && computer.getDiscontinued().toEpochSecond() < 0) {
            throw new IllegalArgumentException("Introduction date must be null or a valid timestamp");
        }

        if (computer.getIntroduced() == null || computer.getIntroduced().isAfter(computer.getDiscontinued())) {
            throw new IllegalArgumentException("Discontinuation date must be superior to the introduction date");
        }
    }

    private void assertCompanyIsNullOrExist(Computer computer) {
        if (computer.getCompanyId() != null && !companyDao.get(computer.getCompanyId()).isPresent()) {
            throw new IllegalArgumentException("Company which introduced the computer must exist");
        }
    }
}
