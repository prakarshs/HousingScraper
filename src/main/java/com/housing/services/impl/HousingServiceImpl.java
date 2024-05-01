package com.housing.services.impl;

import com.housing.entities.Properties;
import com.housing.repositories.PropRepo;
import com.housing.services.HousingService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class HousingServiceImpl implements HousingService {


    private PropRepo propRepo;

    public HousingServiceImpl(PropRepo propRepo) {
        this.propRepo = propRepo;
    }

    @Override
    public List<com.housing.entities.Properties> getProperties() {
        List<com.housing.entities.Properties> properties = new ArrayList<>();
        try {
            // Set the path to ChromeDriver executable
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
            String url = "https://housing.com/in/buy/searches/P2xc23on9yrfbikhj";

            // Initialize ChromeDriver
            WebDriver driver = new ChromeDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            driver.get(url);

            int previousSize = 0;
            int currentSize;

            do {

                previousSize = properties.size();
                // Scroll the page to the bottom to load all elements
                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");


                // Wait for some time for the page to load completely
                Thread.sleep(10000); //adjustable

                Document document = Jsoup.parse(driver.getPageSource());
                Elements propertyElements = document.select("#innerApp > div.css-1io3q4n > div.css-1xg7tbs > div.css-69haxp > div.css-1m1bruh > div");
                for (Element property : propertyElements) {
                    HashMap<String, String> propertyInfo = new LinkedHashMap<>();

                    String averagePrice = "",propertyName = "",propertyLocation = "";

                    String matcher = property.select("[id^=\"srp-\"]> div > div._mkh2mm._9s1txw._ar1bp4._fc1yb4._axkb7n._l8bsq7._vy1x30._gdnqedxx._ft8m16eo.T_47e9c049 > div._h31fwx._vy54a6._5j1sgq.T_4251c589 > div > div > div > button").text();

                    if(matcher.startsWith("Brochure"))
                    {
                        System.out.println("brochure me");
                        averagePrice = property.select("[id^=\"srp-\"] > div > div > div > div > section > div > div:nth-child(2) > div > div").text();
                        averagePrice = averagePrice.substring(13);
                        propertyName = property.select("[id^=\"srp-\"] > div.T_cd31fb10._156v13rr._j6cmky.T_24c887cb._biqgdtch._axkb7n._9s1txw > div._mkh2mm._9s1txw._ar1bp4._fc1yb4._axkb7n._l8bsq7._vy1x30._gdnqedxx._ft8m16eo.T_47e9c049 > div._mkh2mm._1asa1q9c._rlozgrho._2hx11btx.T_6412d3ba > div._9s1txw._gqyh40._0h1q9y.T_8c8ed98f > a > h2").text();
                        propertyLocation = property.select("[id^=\"srp-\"] > div.T_cd31fb10._156v13rr._j6cmky.T_24c887cb._biqgdtch._axkb7n._9s1txw > div._mkh2mm._9s1txw._ar1bp4._fc1yb4._axkb7n._l8bsq7._vy1x30._gdnqedxx._ft8m16eo.T_47e9c049 > div._mkh2mm._1asa1q9c._rlozgrho._2hx11btx.T_6412d3ba > h3 > span").text();

                    }


                    else if (matcher.startsWith("View Phone"))
                    {
                        System.out.println("view phone me");
                        averagePrice = property.select("[id^=\"srp-\"] > div > div> div> div> section > div> div:nth-child(3) > div> div").text();
                        averagePrice = averagePrice.substring(13);
                        propertyName = property.select("[id^=\"srp-\"] > div.T_cd31fb10._156v13rr._j6cmky.T_24c887cb._biqgdtch._axkb7n._9s1txw > div._mkh2mm._9s1txw._ar1bp4._fc1yb4._axkb7n._l8bsq7._vy1x30._gdnqedxx._ft8m16eo.T_47e9c049 > div._mkh2mm._1asa1q9c._rlozgrho._2hx11btx.T_6412d3ba > div._mkh2mm.T_b70307b8 > section > div.T_1e5d5ecb._e214no.T_ad2d45b4._9s1txw.T_9473b29c._5j1tlg._0h1h6o._vy18a8._be1g80._2621jn.item-container > div:nth-child(1) > div._9s1txw._ar1bp4._amkb7n._fc1h6o._r31e5h._gzftgi.T_f121fc5f > a").text();
                        propertyLocation = property.select("[id^=\"srp-\"] > div.T_cd31fb10._156v13rr._j6cmky.T_24c887cb._biqgdtch._axkb7n._9s1txw > div._mkh2mm._9s1txw._ar1bp4._fc1yb4._axkb7n._l8bsq7._vy1x30._gdnqedxx._ft8m16eo.T_47e9c049 > div._mkh2mm._1asa1q9c._rlozgrho._2hx11btx.T_6412d3ba > div._9s1txw._gqyh40._0h1q9y.T_8c8ed98f > a > h2 > div > span").text();
                    }

                    if (averagePrice.isBlank() || propertyName.isBlank()) continue;

                    com.housing.entities.Properties properties1 = new com.housing.entities.Properties();
                    properties1.setAveragePrice(averagePrice);
                    properties1.setPropertyName(propertyName);
                    properties1.setPropertyLocation(propertyLocation);
                    properties.add(properties1);

                    checkDuplicate(properties1);

                }
                currentSize = properties.size();
            }while (currentSize > previousSize);

            driver.quit();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    @Override
    public List<Properties> searchBarProperties() {

        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get("https://housing.com/");

        ArrayList<String> propertyNames = new ArrayList<>();
        propertyNames.add("Smart World Orchard");
        // Loop through property names
        for (String propertyName : propertyNames) {
            // Find the search bar element and enter the property name
            WebElement searchBar = driver.findElement(By.cssSelector("#innerApp > div> div > div > div> div > div > input"));
            searchBar.sendKeys(propertyName);

            // Add a 3-second delay
            try {
                Thread.sleep(3000); // 3000 milliseconds = 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Submit the search query
            searchBar.sendKeys(Keys.ENTER);

            // Wait for search results to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#innerApp > div > div> div > div > div > div > div> div > h1")));


            // Parse search results
            List<WebElement> priceElements = driver.findElements(By.cssSelector("#innerApp > div > div > div > div > div > div > div > span.css-124qey8"));
            List<String> prices = new ArrayList<>();
            for (WebElement priceElement : priceElements) {
                prices.add(priceElement.getText());
            }

            // Print or store the prices
            System.out.println("Prices : " + prices);
        }

// Close WebDriver session
        driver.quit();

        return null;
    }

    private void checkDuplicate(com.housing.entities.Properties properties1) {

        Properties properties = this.propRepo.findByPropertyName(properties1.getPropertyName()).orElse(null);
        if (properties == null) {
            this.propRepo.save(properties1);
        }

    }



}
