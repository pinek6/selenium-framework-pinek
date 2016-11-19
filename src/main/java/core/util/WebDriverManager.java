package core.util;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Created by Laptok on 2016-11-19.
 */
public class WebDriverManager {
    static ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

    private static String baseUrl = "";

    private WebDriverManager() {
    }

    public static WebDriver getDriver() {
        return webDriver.get();
    }

    public static void setWebDriver(WebDriver driver) {
        webDriver.set(driver);
    }

    public static void switchToLastTab() {
        ArrayList<String> tabs = new ArrayList<>(
                webDriver.get().getWindowHandles());
        getDriver().switchTo().window(tabs.get(tabs.size() - 1));
    }

    public static void quit() {
        getDriver().quit();
        //setWebDriver(null);
    }

    public static void close() {
        getDriver().close();
    }

    public static void openWithBaseUrl(String url) {
        getDriver().get(baseUrl + url);
    }

    /**
     * creates a driver using LocalDriverFactory fields for browser and hubUrl.
     */
    public static void createDriver() {
        setWebDriver(LocalDriverFactory.createInstance());
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String url) {
        baseUrl = url;
    }

    public static void open(String link) {
        getDriver().get(link);
    }

    public static WebDriverWait waitForElementWithDefaultTimeout() {
        return waitForElement(60);
    }

    public static WebDriverWait waitForElement(int timeOut) {
        return new WebDriverWait(getDriver(), timeOut);
    }

    /**
     * for debug purposes
     */
    public static void takeScreenShot() {
        File screenShoot = ((TakesScreenshot) WebDriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
        try {
            String screenShotPath = Paths.get(String.format("%s/screenShots/%s_%d.png", TestData.DOCUMENT_LOCATION,
                    "debug_pic", System.currentTimeMillis() / 1000)).toString();
            FileUtils.copyFile(screenShoot, new File(screenShotPath));
            Reporter.log(String.format("Check screenshot at: %s%n", screenShotPath), true);
            Reporter.log(String.format("Page URL where error occurred: %s%n", WebDriverManager.getDriver().getCurrentUrl()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches to a new popup.
     */
    public static void switchToNewPopup() {
        String mwh = webDriver.get().getWindowHandle();
        Set<String> handles = webDriver.get().getWindowHandles();

        for (String handle : handles) {
            if (!handle.contains(mwh)) {
                webDriver.get().switchTo().window(handle);
            }
        }
    }

    public static void disableImplicitWait() {
        getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    public static void enableImplicitWait() {
        getDriver().manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }
}
