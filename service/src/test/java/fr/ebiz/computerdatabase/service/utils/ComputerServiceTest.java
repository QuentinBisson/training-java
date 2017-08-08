package fr.ebiz.computerdatabase.service.utils;

import fr.ebiz.computerdatabase.core.Company;
import fr.ebiz.computerdatabase.core.Computer;
import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.mapper.ComputerMapper;
import fr.ebiz.computerdatabase.persistence.SortOrder;
import fr.ebiz.computerdatabase.persistence.config.RepositoryConfiguration;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.config.ServiceConfiguration;
import fr.ebiz.computerdatabase.service.utils.utils.SpringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepositoryConfiguration.class, ServiceConfiguration.class}, loader = AnnotationConfigContextLoader.class)
public class ComputerServiceTest {

    private static final int PAGE_SIZE = 10;

    @Mock
    private ComputerMapper mockComputerMapper;
    @Mock
    private ComputerDao computerDao;
    @Mock
    private CompanyService companyService;

    @Autowired
    @InjectMocks
    private ComputerService computerService;
    @Autowired
    private ComputerMapper computerMapper;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        ComputerService computerService = (ComputerService) SpringUtils.unwrapProxy(this.computerService);
        ReflectionTestUtils.setField(computerService, "computerDao", computerDao);
    }

    @Test
    public void testGetWorksWithExistingId() {
        Computer computer = Computer.builder().id(1).name("Test").build();
        Mockito.when(computerDao.get(computer.getId())).thenReturn(Optional.of(computer));
        Mockito.when(mockComputerMapper.toDto(computer)).thenReturn(computerMapper.toDto(computer));
        Assert.assertEquals(computer.getId(), computerService.get(1).get().getId());
    }

    @Test
    public void testGetHandlesMissingId() {
        Mockito.when(computerDao.get(1)).thenReturn(Optional.empty());

        Assert.assertFalse(computerService.get(1).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsIAEWhenIdEqualsZero() {
        computerService.get(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsIAEOnNegativeId() {
        computerService.get(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNullPageable() {
        computerService.getAll(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNegativeElements() {
        computerService.getAll(GetAllComputersRequest.builder().pageSize(Integer.MIN_VALUE).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNegativePage() {
        computerService.getAll(GetAllComputersRequest.builder().pageSize(PAGE_SIZE).page(-1).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithTooBigPageNumberWithFullLastPage() {
        Mockito.when(computerDao.count("")).thenReturn(100);
        computerService.getAll(GetAllComputersRequest.builder().pageSize(PAGE_SIZE).page(11).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithTooBigPageNumberWithoutFullLastPage() {
        Mockito.when(computerDao.count("")).thenReturn(101);
        computerService.getAll(GetAllComputersRequest.builder().pageSize(PAGE_SIZE).page(12).build());
    }

    @Test
    public void testGetAllWorks() {
        int elements = 7;
        List<Computer> computers = IntStream.range(0, elements)
                .mapToObj(index -> Computer.builder().id(index).name("computer" + index).build())
                .collect(Collectors.toList());

        Mockito.when(computerDao.count("")).thenReturn(elements);
        Pageable pageable = Pageable.builder().elements(PAGE_SIZE).page(0).build();
        List<Computer> pagedComputers = computers.subList(0, elements);
        GetAllComputersRequest request = GetAllComputersRequest.builder().pageSize(pageable.getElements()).page(pageable.getPage()).query("").column(ComputerDao.SortColumn.NAME).order(SortOrder.ASC).build();
        Mockito.when(computerDao.getAll(request.getQuery(), request.getPageSize(), request.getOffset(), request.getColumn(), request.getOrder())).thenReturn(pagedComputers);

        for (int i = 0; i < pagedComputers.size(); i++) {
            Mockito.when(mockComputerMapper.toDto(pagedComputers)).thenReturn(computerMapper.toDto(pagedComputers));
        }

        Page<ComputerDto> page = computerService.getAll(request);
        Assert.assertEquals(0, page.getCurrentPage());
        Assert.assertEquals(1, page.getTotalPages());
        for (int i = 0; i < pagedComputers.size(); i++) {
            Assert.assertEquals(pagedComputers.get(i).getId(), page.getElements().get(i).getId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertHandleNull() {
        computerService.insert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertHandleComputerWithId() {
        ComputerDto computer = ComputerDto
                .builder()
                .id(1)
                .build();

        computerService.insert(computer);
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

        Mockito.when(companyService.get(company.getId())).thenReturn(Optional.of(company));
        Mockito.doNothing().when(computerDao).insert(Mockito.any(Computer.class));

        computerService.insert(computer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateHandleNull() {
        computerService.update(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateHandleNullId() {
        computerService.update(new ComputerDto());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateHandleNonExistingComputer() {
        ComputerDto computer = ComputerDto.builder().id(1).build();

        Mockito.when(computerDao.get(1)).thenReturn(Optional.empty());

        computerService.update(computer);
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

        Mockito.when(companyService.get(company.getId())).thenReturn(Optional.of(company));
        Mockito.when(computerDao.get(computer.getId())).thenReturn(Optional.of(computerMapper.toEntity(computer)));
        Mockito.doNothing().when(computerDao).update(Mockito.any(Computer.class));

        computerService.update(computer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteHandleNull() {
        computerService.delete(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteHandleNullId() {
        computerService.delete(new ComputerDto());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteHandleNonExistingComputer() {
        Mockito.when(computerDao.get(Integer.MAX_VALUE)).thenReturn(Optional.empty());

        computerService.delete(ComputerDto.builder().id(Integer.MAX_VALUE).build());
    }

    @Test
    public void testDeleteWorks() {
        Computer computer = Computer.builder().id(1).build();

        Mockito.when(computerDao.get(1)).thenReturn(Optional.of(computer));
        Mockito.doNothing().when(computerDao).delete(computer.getId());

        computerService.delete(computerMapper.toDto(computer));
    }

}
