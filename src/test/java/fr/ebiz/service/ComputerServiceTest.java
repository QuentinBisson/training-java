package fr.ebiz.service;

import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.service.impl.ComputerServiceImpl;
import fr.ebiz.computerdatabase.service.impl.paging.Page;
import fr.ebiz.computerdatabase.service.impl.paging.Pageable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComputerServiceTest {

    public static final int ELEMENTS_PER_PAGE = 10;
    @Mock
    private ComputerDao computerDao;

    @InjectMocks
    private ComputerServiceImpl service;

    @Test
    public void testGetWorksWithExistingId() {
        Computer computer = Computer.builder().id(1).name("Test").build();
        when(computerDao.get(1)).thenReturn(Optional.of(computer));

        Assert.assertEquals(service.get(1).get(), computer);
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
        service.getAll(Pageable.builder().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNegativeElements() {
        service.getAll(Pageable.builder().elements(Integer.MIN_VALUE).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithNegativePage() {
        service.getAll(Pageable.builder().elements(10).page(-1).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithTooBigPageNumberWithFullLastPage() {
        when(computerDao.count()).thenReturn(100);
        service.getAll(Pageable.builder().elements(ELEMENTS_PER_PAGE).page(11).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllWithTooBigPageNumberWithoutFullLastPage() {
        when(computerDao.count()).thenReturn(101);
        service.getAll(Pageable.builder().elements(ELEMENTS_PER_PAGE).page(12).build());
    }

    @Test
    public void testGetAllWorks() {
        int elements = 7;
        List<Computer> computers = IntStream.range(0, elements)
                .mapToObj(index -> Computer.builder().name("computer" + index).build())
                .collect(Collectors.toList());

        when(computerDao.count()).thenReturn(elements);
        Pageable pageable = Pageable.builder().elements(ELEMENTS_PER_PAGE).page(0).build();
        List<Computer> pagedComputers = computers.subList(0, elements);
        when(computerDao.getAll(pageable.getElements(), pageable.getPage() * pageable.getElements())).thenReturn(pagedComputers);

        Page<Computer> page = service.getAll(pageable);
        Assert.assertEquals(0, page.getCurrentPage());
        Assert.assertEquals(1, page.getTotalPages());
        for (int i = 0; i < pagedComputers.size(); i++) {
            Assert.assertEquals(pagedComputers.get(i), page.getElements().get(i));
        }
    }

}
