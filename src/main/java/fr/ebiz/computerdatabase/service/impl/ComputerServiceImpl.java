package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.PagingUtils;
import fr.ebiz.computerdatabase.mapper.ComputerMapper;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.validator.impl.ComputerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ComputerServiceImpl implements ComputerService {

    private final ComputerDao computerDao;
    private final ComputerMapper computerMapper;
    private final ComputerValidator computerValidator;

    /**
     * Constructor.
     *
     * @param computerDao       Dao used to access the computers
     * @param computerMapper    The computer mapper
     * @param computerValidator The computer validator
     */
    @Autowired
    public ComputerServiceImpl(ComputerDao computerDao, ComputerMapper computerMapper, ComputerValidator computerValidator) {
        this.computerDao = computerDao;
        this.computerMapper = computerMapper;
        this.computerValidator = computerValidator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ComputerDto> get(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be > 0");
        }

        return computerDao.get(id).map(c -> Optional.of(ComputerMapper.getInstance().toDto(c)))
                .orElse(Optional.empty());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    @Transactional(readOnly = true)
    @Override
    public Page<ComputerDto> getAll(GetAllComputersRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Pagination object is null");
        }

        if (request.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page size must be > 0");
        }

        Integer numberOfComputers = computerDao.count(request.getQuery());
        Integer totalPage = PagingUtils.countPages(request.getPageSize(), numberOfComputers);
        if (request.getPage() < 0 || request.getPage() >= totalPage) {
            throw new IllegalArgumentException("Page number must be [0-" + totalPage + "]");
        }

        List<Computer> computers;
        if (totalPage == 0) {
            computers = Collections.emptyList();
        } else {
            computers = computerDao.getAll(request);
        }

        Page<ComputerDto> page = Page.builder()
                .currentPage(request.getPage())
                .totalPages(totalPage)
                .totalElements(numberOfComputers)
                .elements(ComputerMapper.getInstance().toDto(computers))
                .build();

        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public int insert(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        if (dto.getId() != null) {
            throw new IllegalArgumentException("Computer should not have an id");
        }

        computerValidator.validate(dto);
        Computer computer = computerMapper.toEntity(dto);
        computerDao.insert(computer);
        return computer.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional()
    @Override
    public void update(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        assertComputerIdIsNotNullAndExists(dto.getId());

        computerValidator.validate(dto);
        computerDao.update(computerMapper.toEntity(dto));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void delete(int computerId) {
        assertComputerIdIsNotNullAndExists(computerId);

        computerDao.delete(computerId);
    }

    @Transactional
    @Override
    public void deleteByCompanyId(int companyId) {
        computerDao.deleteByCompanyId(companyId);
    }

    @Transactional
    @Override
    public void deleteComputers(List<Integer> ids) {
        computerDao.deleteComputers(ids);
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
     * @param computerId The computer to test
     */
    private void assertComputerIdIsNotNullAndExists(int computerId) {
        if (!computerDao.get(computerId).isPresent()) {
            throw new IllegalArgumentException("Computer should have an id and exist in the db");
        }
    }

}
