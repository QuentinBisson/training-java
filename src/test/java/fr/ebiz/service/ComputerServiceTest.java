package fr.ebiz.service;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.mapper.ComputerMapper;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.GetAllComputersRequest;
import fr.ebiz.computerdatabase.persistence.dao.SortOrder;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import fr.ebiz.computerdatabase.service.impl.ComputerServiceImpl;
import fr.ebiz.computerdatabase.service.validator.exception.ValidationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComputerServiceTest {

    private static final int PAGE_SIZE = 10;
    private final ComputerMapper computerMapper;
    @Mock
    private ComputerDao computerDao;
    @Mock
    private CompanyDao companyDao;
    @Mock
    private DataSource dataSource;
    @Mock
    private TransactionManager transactionManager;
    @InjectMocks
    private ComputerServiceImpl service;

    public ComputerServiceTest() {
        this.computerMapper = new ComputerMapper();
    }

    @Test
    public void testGetWorksWithExistingId() {
        Computer computer = Computer.builder().id(1).name("Test").build();
        when(computerDao.get(1)).thenReturn(Optional.of(computer));

        Assert.assertEquals(computer.getId(), service.get(1).get().getId());
    }

    @Test
    public void testGetHandlesMissingId() {
        when(computerDao.get(1)).thenReturn(Optional.empty());

        Assert.assertEquals(service.get(1), Optional.empty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsIAEWhenIdEqualsZero() {
        service.get(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsIAEOnNegativeId() {
        service.get(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNullPageable() {
        service.getAll(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNoRequestedElements() {
        service.getAll(GetAllComputersRequest.builder().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNegativeElements() {
        service.getAll(GetAllComputersRequest.builder().pageSize(Integer.MIN_VALUE).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNegativePage() {
        service.getAll(GetAllComputersRequest.builder().pageSize(PAGE_SIZE).page(-1).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithTooBigPageNumberWithFullLastPage() {
        when(computerDao.count("")).thenReturn(100);
        service.getAll(GetAllComputersRequest.builder().pageSize(PAGE_SIZE).page(11).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithTooBigPageNumberWithoutFullLastPage() {
        when(computerDao.count("")).thenReturn(101);
        service.getAll(GetAllComputersRequest.builder().pageSize(PAGE_SIZE).page(12).build());
    }

    @Test
    public void testGetAllWorks() {
        int elements = 7;
        List<Computer> computers = IntStream.range(0, elements)
                .mapToObj(index -> Computer.builder().id(index).name("computer" + index).build())
                .collect(Collectors.toList());

        when(computerDao.count("")).thenReturn(elements);
        Pageable pageable = Pageable.builder().elements(PAGE_SIZE).page(0).build();
        List<Computer> pagedComputers = computers.subList(0, elements);
        when(computerDao.getAll(Mockito.any(GetAllComputersRequest.class))).thenReturn(pagedComputers);

        Page<ComputerDto> page = service.getAll(GetAllComputersRequest.builder().pageSize(pageable.getElements()).page(pageable.getPage()).query("").order(SortOrder.ASC).column(ComputerDao.SortColumn.NAME).build());
        Assert.assertEquals(0, page.getCurrentPage());
        Assert.assertEquals(1, page.getTotalPages());
        for (int i = 0; i < pagedComputers.size(); i++) {
            Assert.assertEquals(pagedComputers.get(i).getId(), page.getElements().get(i).getId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertHandleNull() {
        service.insert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertHandleComputerWithId() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .build();

        service.insert(computer);
    }

    @Test(expected = ValidationException.class)
    public void testInsertHandleNullName() {
        ComputerDto computer = ComputerDto
                .builder()
                .build();

        service.insert(computer);
    }

    @Test(expected = ValidationException.class)
    public void testInsertHandleEmptyName() {
        ComputerDto computer = ComputerDto
                .builder()
                .name("   ")
                .build();

        service.insert(computer);
    }

    @Test(expected = ValidationException.class)
    public void testInsertHandleInvalidTimestampForIntroduced() {
        ComputerDto computer = ComputerDto
                .builder()
                .name("test")
                .introduced(LocalDate.MIN)
                .build();

        service.insert(computer);
    }

    @Test(expected = ValidationException.class)
    public void testInsertHandleIntroducedAfterNow() {
        ComputerDto computer = ComputerDto
                .builder()
                .name("test")
                .introduced(LocalDate.MAX)
                .build();

        service.insert(computer);
    }

    @Test(expected = ValidationException.class)
    public void testInsertHandleInvalidTimestampForDiscontinued() {
        ComputerDto computer = ComputerDto
                .builder()
                .name("test")
                .introduced(LocalDate.now())
                .discontinued(LocalDate.MIN)
                .build();

        service.insert(computer);
    }

    @Test(expected = ValidationException.class)
    public void testInsertHandleDiscontinuedAfterNow() {
        ComputerDto computer = ComputerDto
                .builder()
                .name("test")
                .introduced(LocalDate.now())
                .discontinued(LocalDate.MAX)
                .build();

        service.insert(computer);
    }

    @Test(expected = ValidationException.class)
    public void testInsertHandleDiscontinuedBeforeIntroduced() {
        ComputerDto computer = ComputerDto
                .builder()
                .name("test")
                .introduced(LocalDate.now())
                .discontinued(LocalDate.now().minusDays(1))
                .build();

        service.insert(computer);
    }

    @Test(expected = ValidationException.class)
    public void testInsertHandleUnknownCompany() {
        ComputerDto computer = ComputerDto
                .builder()
                .name("test")
                .introduced(LocalDate.now().minusDays(1))
                .discontinued(LocalDate.now())
                .companyId(1)
                .build();

        when(companyDao.get(1)).thenReturn(Optional.empty());

        service.insert(computer);
    }

    @Test
    public void testInsertWorks() {
        Company company = Company
                .builder()
                .id(1)
                .build();

        ComputerDto computer = ComputerDto
                .builder()
                .name("test")
                .introduced(LocalDate.now().minusDays(1))
                .discontinued(LocalDate.now())
                .companyId(company.getId())
                .build();

        when(companyDao.get(1)).thenReturn(Optional.of(company));
        when(computerDao.insert(computerMapper.toEntity(computer))).thenReturn(true);

        service.insert(computer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateHandleNull() {
        service.update(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateHandleNullId() {
        service.update(new ComputerDto());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateHandleNonExistingComputer() {
        ComputerDto computer = ComputerDto.builder().id(1).build();

        when(computerDao.get(1)).thenReturn(Optional.empty());

        service.update(computer);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateHandleNullName() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));

        service.update(computer);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateHandleEmptyName() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .name("   ")
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));

        service.update(computer);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateHandleInvalidTimestampForIntroduced() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .name("test")
                .introduced(LocalDate.MIN)
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));

        service.update(computer);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateHandleIntroducedAfterNow() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .name("test")
                .introduced(LocalDate.MAX)
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));

        service.update(computer);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateHandleInvalidTimestampForDiscontinued() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .name("test")
                .introduced(LocalDate.now())
                .discontinued(LocalDate.MIN)
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));

        service.update(computer);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateHandleDiscontinuedAfterNow() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .name("test")
                .introduced(LocalDate.now())
                .discontinued(LocalDate.MAX)
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));

        service.update(computer);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateHandleDiscontinuedBeforeIntroduced() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .name("test")
                .introduced(LocalDate.now())
                .discontinued(LocalDate.now().minusDays(1))
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));

        service.update(computer);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateHandleUnknownCompany() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .name("test")
                .introduced(LocalDate.now().minusDays(1))
                .discontinued(LocalDate.now())
                .companyId(1)
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));
        when(companyDao.get(1)).thenReturn(Optional.empty());

        service.update(computer);
    }

    @Test
    public void testUpdateWorks() {
        Company company = Company
                .builder()
                .id(1)
                .build();

        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .name("test")
                .introduced(LocalDate.now().minusDays(1))
                .discontinued(LocalDate.now())
                .companyId(company.getId())
                .build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));
        when(companyDao.get(1)).thenReturn(Optional.of(company));
        when(computerDao.update(Mockito.any(Computer.class))).thenReturn(true);

        service.update(computer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteHandleNull() {
        service.delete(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteHandleNullId() {
        service.delete(new ComputerDto());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteHandleNonExistingComputer() {
        when(computerDao.get(Integer.MAX_VALUE)).thenReturn(Optional.empty());

        service.delete(ComputerDto.builder().id(Integer.MAX_VALUE).build());
    }

    @Test
    public void testDeleteWorks() {
        ComputerDto computer = ComputerDto.builder().id(1).build();

        when(computerDao.get(1)).thenReturn(Optional.of(computerMapper.toEntity(computer)));
        when(computerDao.delete(1)).thenReturn(true);

        service.delete(computer);
    }

}
