package com.selenium.template;


import com.selenium.template.automationFramework.CommonTasks;
import com.selenium.template.automationFramework.Log4Test;
import com.selenium.template.config.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriverBase {

    private static List<DriverFactory> webDriverThreadPool = Collections.synchronizedList(new ArrayList<DriverFactory>());
    private static ThreadLocal<DriverFactory> driverFactory;

    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driverFactory = new ThreadLocal<DriverFactory>() {
            @Override
            protected DriverFactory initialValue() {
                DriverFactory driverFactory = new DriverFactory();
                webDriverThreadPool.add(driverFactory);
                return driverFactory;
            }
        };
    }

    public static WebDriver getDriver() throws Exception {
        return driverFactory.get().getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public static void clearCookies() throws Exception {
        getDriver().manage().deleteAllCookies();
    }

    //Comenteaza numai cand rulezi pe local pt debug / cand rulezi suita prin jenkins decomenteaza ca altfel o sa ai 1000 de browsere deschise
    @AfterSuite(alwaysRun = true)
    public static void closeDriverObjects() {
        for (DriverFactory driverFactory : webDriverThreadPool) {
            driverFactory.quitDriver();
        }
    }

    public void setup (String TestId, String urlPath)throws Exception{
        Log4Test.info("Test ID: "+ TestId);
        CommonTasks.goToUrl(getDriver(), urlPath);
        getDriver().manage().window().maximize();
    }
}