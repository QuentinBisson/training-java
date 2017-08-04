package fr.ebiz.service;

import fr.ebiz.computerdatabase.config.ServiceConfiguration;
import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.mapper.ComputerMapper;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.SortOrder;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.util.SpringUtils;
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

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceConfiguration.class, loader = AnnotationConfigContextLoader.class)
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
    private ComputerService service;
    @Autowired
    private ComputerMapper computerMapper;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        ComputerService computerService = (ComputerService) SpringUtils.unwrapProxy(service);
        ReflectionTestUtils.setField(computerService, "computerDao", computerDao);
    }

    @Test
    public void testGetWorksWithExistingId() {
        Computer computer = Computer.builder().id(1).name("Test").build();
        when(computerDao.get(computer.getId())).thenReturn(Optional.of(computer));
        when(mockComputerMapper.toDto(computer)).thenReturn(computerMapper.toDto(computer));
        Assert.assertEquals(computer.getId(), service.get(1).get().getId());
    }

    @Test
    public void testGetHandlesMissingId() {
        when(computerDao.get(1)).thenReturn(Optional.empty());

        Assert.assertFalse(service.get(1).isPresent());
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
        Pageable pageable = Pageable.builder().pageSize(PAGE_SIZE).page(0).build();
        List<Computer> pagedComputers = computers.subList(0, elements);
        GetAllComputersRequest request = GetAllComputersRequest.builder().pageSize(pageable.getPageSize()).page(pageable.getPage()).query("").column(ComputerDao.SortColumn.NAME).order(SortOrder.ASC).build();
        when(computerDao.getAll(request.getQuery(), request.getPageSize(), request.getOffset(), request.getColumn(), request.getOrder())).thenReturn(pagedComputers);

        for (int i = 0; i < pagedComputers.size(); i++) {
            when(mockComputerMapper.toDto(pagedComputers)).thenReturn(computerMapper.toDto(pagedComputers));
        }

        Page<ComputerDto> page = service.getAll(request);
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

        when(companyService.get(company.getId())).thenReturn(Optional.of(company));
        when(computerDao.insert(mockComputerMapper.toEntity(computer))).thenReturn(true);

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

        when(companyService.get(company.getId())).thenReturn(Optional.of(company));
        when(computerDao.get(computer.getId())).thenReturn(Optional.of(computerMapper.toEntity(computer)));
        when(computerDao.update(Mockito.any(Computer.class))).thenReturn(true);

        service.update(computer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteHandleNonExistingComputer() {
        when(computerDao.get(Integer.MAX_VALUE)).thenReturn(Optional.empty());

        service.delete(Integer.MAX_VALUE);
    }

    @Test
    public void testDeleteWorks() {
        Computer computer = Computer.builder().id(1).build();

        when(computerDao.get(computer.getId())).thenReturn(Optional.of(computer));
        when(computerDao.delete(computer.getId())).thenReturn(true);

        service.delete(computer.getId());
    }

}
