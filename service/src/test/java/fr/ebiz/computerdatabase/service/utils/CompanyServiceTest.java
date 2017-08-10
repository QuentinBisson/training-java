package fr.ebiz.computerdatabase.service.utils;

import fr.ebiz.computerdatabase.core.Company;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.persistence.config.RepositoryConfiguration;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.service.CompanyService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepositoryConfiguration.class, ServiceConfiguration.class}, loader = AnnotationConfigContextLoader.class)
public class CompanyServiceTest {

    private static final int ELEMENTS_PER_PAGE = 10;
    @Mock
    private CompanyDao companyDao;

    @Autowired
    @InjectMocks
    private CompanyService service;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        CompanyService companyService = (CompanyService) SpringUtils.unwrapProxy(service);
        ReflectionTestUtils.setField(companyService, "companyDao", companyDao);
    }

    @Test
    public void testGetWorksWithExistingId() {
        Company company = Company.builder().id(1).name("Test").build();
        Mockito.when(companyDao.get(company.getId())).thenReturn(Optional.of(company));

        Assert.assertEquals(company, service.get(company.getId()).get());
    }

    @Test
    public void testGetHandlesMissingId() {
        Mockito.when(companyDao.get(1)).thenReturn(Optional.empty());

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
        service.getAll(Pageable.builder().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNegativeElements() {
        service.getAll(Pageable.builder().pageSize(Integer.MIN_VALUE).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNegativePage() {
        service.getAll(Pageable.builder().pageSize(10).page(-1).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithTooBigPageNumberWithFullLastPage() {
        Mockito.when(companyDao.count()).thenReturn(100);
        service.getAll(Pageable.builder().pageSize(ELEMENTS_PER_PAGE).page(11).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithTooBigPageNumberWithoutFullLastPage() {
        Mockito.when(companyDao.count()).thenReturn(101);
        service.getAll(Pageable.builder().pageSize(ELEMENTS_PER_PAGE).page(12).build());
    }

    @Test
    public void testGetAllWorks() {
        int elements = 15;
        List<Company> companies = IntStream.range(0, elements)
                .mapToObj(index -> Company.builder().name("company" + index).build())
                .collect(Collectors.toList());

        Mockito.when(companyDao.count()).thenReturn(elements);
        Pageable pageable = Pageable.builder().pageSize(ELEMENTS_PER_PAGE).page(1).build();
        List<Company> pagedCompanies = companies.subList(ELEMENTS_PER_PAGE, elements);
        Mockito.when(companyDao.getAll(pageable.getPageSize(), pageable.getPage() * pageable.getPageSize())).thenReturn(pagedCompanies);

        Page<Company> page = service.getAll(pageable);
        Assert.assertEquals(1, page.getCurrentPage());
        Assert.assertEquals(2, page.getTotalPages());
        for (int i = 0; i < pagedCompanies.size(); i++) {
            Assert.assertEquals(pagedCompanies.get(i), page.getElements().get(i));
        }
    }

}
