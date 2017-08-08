package fr.ebiz.computerdatabase.web;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

final class SeleniumUtils {

    static void input(WebDriver driver, By byElement, String input) {
        driver.findElement(byElement).clear();
        driver.findElement(byElement).sendKeys(input);
    }

    static String alert(WebDriver driver, boolean accept) {
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        if (accept) {
            alert.accept();
        } else {
            alert.dismiss();
        }
        return alertText;
    }

    static WebDriver startDriver() {
        FirefoxDriverManager.getInstance().setup();

        WebDriver driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return driver;
    }

    static Properties readTestProperties() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream propertyFileStream = classLoader.getResourceAsStream("test.properties");
        Properties properties = new Properties();
        properties.load(propertyFileStream);
        return properties;
    }
}
