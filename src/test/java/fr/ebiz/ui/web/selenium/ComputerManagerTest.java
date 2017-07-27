package fr.ebiz.ui.web.selenium;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Properties;

public class ComputerManagerTest {

    private static final String INDEX_URL = "/";
    private static final By SEARCHBOX_BY = By.id("searchbox");
    private static final By SEARCH_SUBMIT_BY = By.id("searchsubmit");
    private static final By EDIT_COMPUTER_BY = By.id("editComputer");
    private static final By COMPUTER_NAME_BY = By.id("computerName");
    private static final By DISCONTINUED_BY = By.id("discontinued");
    private static final By HOME_TITLE_BY = By.id("homeTitle");
    private static final By INTRODUCED_BY = By.id("introduced");

    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        driver = SeleniumUtils.startDriver();

        Properties properties = SeleniumUtils.readTestProperties();
        baseUrl = properties.getProperty("web-app.url");
    }

    @Test
    public void testCreateEditAndDelete() throws Exception {
        driver.get(baseUrl + INDEX_URL);

        // add a computer
        addComputer();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(HOME_TITLE_BY));
        Assert.assertEquals(baseUrl + INDEX_URL, driver.getCurrentUrl());

        // Search computer and edit it
        searchAndEditComputer();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(HOME_TITLE_BY));
        Assert.assertEquals(baseUrl + INDEX_URL, driver.getCurrentUrl());

        // Delete a computer
        deleteComputer();
    }

    private void deleteComputer() {
        String searchParameter = "Ordinateur";
        SeleniumUtils.input(driver, SEARCHBOX_BY, searchParameter);

        driver.findElement(SEARCH_SUBMIT_BY).click();
        driver.findElement(EDIT_COMPUTER_BY).click();

        driver.findElement(By.name("cb")).click();
        driver.findElement(By.xpath("//a[@id='deleteSelected']/i")).click();
        Assert.assertTrue(SeleniumUtils.alert(driver, true).matches("^Are you sure you want to delete the selected computers[\\s\\S]$"));
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(HOME_TITLE_BY));
        Assert.assertEquals(baseUrl + INDEX_URL + "home", driver.getCurrentUrl().replace("#", ""));
    }

    private void searchAndEditComputer() {
        SeleniumUtils.input(driver, SEARCHBOX_BY, "Nouvel or");

        driver.findElement(SEARCH_SUBMIT_BY).click();
        driver.findElement(By.linkText("Nouvel ordinateur")).click();

        Assert.assertTrue(driver.getCurrentUrl().matches(baseUrl + "/computers\\?id=\\d+"));

        new Select(driver.findElement(By.id("companyId"))).selectByVisibleText("BBN Technologies");
        SeleniumUtils.input(driver, COMPUTER_NAME_BY, "Ordinateur edite");
        driver.findElement(By.cssSelector("input.btn.btn-primary")).click();
    }


    private void addComputer() {
        driver.findElement(By.id("addComputer")).click();

        Assert.assertEquals(baseUrl + "/computers", driver.getCurrentUrl());

        SeleniumUtils.input(driver, COMPUTER_NAME_BY, "Nouvel ordinateur");
        SeleniumUtils.input(driver, INTRODUCED_BY, "04/07/2013");
        SeleniumUtils.input(driver, DISCONTINUED_BY, "04/07/2012");
        new Select(driver.findElement(By.id("companyId"))).selectByVisibleText("Amiga Corporation");

        driver.findElement(By.cssSelector("input.btn.btn-primary")).click();
        SeleniumUtils.input(driver, DISCONTINUED_BY, "04/07/2015");
        driver.findElement(By.cssSelector("input.btn.btn-primary")).click();
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}
