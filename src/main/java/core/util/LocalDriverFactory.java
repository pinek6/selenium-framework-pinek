package core.util;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Laptok on 2016-11-19.
 */
public class LocalDriverFactory {

    public static String hubUrl;
    public static String browserName;

    public static BrowserMobProxy proxy;

    public static BrowserMobProxy getProxy(){
        return proxy;
    }
    public static void newHar(BrowserMobProxy p){
        p.newHar();
    }

    public static void newHar(){
        proxy.newHar();
    }

    public static void startProxy(BrowserMobProxy p){
        p.start(0);
    }

    static WebDriver createInstance() {
        WebDriver driver;
        if (browserName.toLowerCase().contains("firefox")) {
            FirefoxProfile fp = new FirefoxProfile();
            fp.setPreference("browser.startup.homepage", "about:blank");
            fp.setPreference("startup.homepage_welcome_url", "about:blank");
            fp.setPreference("startup.homepage_welcome_url.additional", "about:blank");
            DesiredCapabilities capabilities = DesiredCapabilities.firefox();
            capabilities.setCapability(FirefoxDriver.PROFILE, fp);
            capabilities.setCapability(CapabilityType.OVERLAPPING_CHECK_DISABLED, true);

            driver = new FirefoxDriver(capabilities);
            driver.manage().window().setSize(new Dimension(1280, 1024));
            return driver;
        }
        if (browserName.toLowerCase().contains("internet")) {
            driver = new InternetExplorerDriver();
            return driver;
        }
        if (browserName.toLowerCase().contains("chrome")) {
            System.setProperty("webdriver.chrome.driver", "C:\\selenium\\chromedriver.exe");
            driver = new ChromeDriver();
            return driver;
        }
        if (browserName.toLowerCase().contains("phantom")) {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setJavascriptEnabled(true);
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[]{"--web-security=no", "--ignore-ssl-errors=yes"});
            driver = new PhantomJSDriver(caps);
            driver.manage().window().setSize(new Dimension(1280, 1024));
            return driver;
        }
        if (browserName.toLowerCase().contains("docker-firefox")) {
            if (hubUrl == null) throw new AssertionError("hubUrl parameter is null");
            try {
                FirefoxProfile fp = new FirefoxProfile();
                fp.setPreference("browser.startup.homepage", "about:blank");
                fp.setPreference("startup.homepage_welcome_url", "about:blank");
                fp.setPreference("startup.homepage_welcome_url.additional", "about:blank");
                fp.setAssumeUntrustedCertificateIssuer(false);
                DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability(FirefoxDriver.PROFILE, fp);
                capabilities.setCapability(CapabilityType.OVERLAPPING_CHECK_DISABLED, true);
                RemoteWebDriver remoteWebDriver = new RemoteWebDriver(
                        new URL(hubUrl),
                        capabilities);
                remoteWebDriver.setFileDetector(new LocalFileDetector());
                remoteWebDriver.manage().window().setSize(new Dimension(1280, 1024));
                return remoteWebDriver;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        if (browserName.toLowerCase().contains("docker-chrome")) {
            if (hubUrl == null) throw new AssertionError("hubUrl parameter is null");
            try {
                proxy = new BrowserMobProxyServer();
                proxy.setTrustAllServers(true);
                proxy.start(0);
                Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
                RemoteWebDriver remoteWebDriver = new RemoteWebDriver(
                        new URL(hubUrl),
                        capabilities);
                proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
                //newHar(proxy);
                remoteWebDriver.setFileDetector(new LocalFileDetector());
                remoteWebDriver.manage().window().setSize(new Dimension(1280, 1024));
                return remoteWebDriver;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
