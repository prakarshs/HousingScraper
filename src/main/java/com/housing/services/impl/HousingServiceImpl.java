package com.housing.services.impl;

import com.housing.entities.Properties;
import com.housing.repositories.PropRepo;
import com.housing.services.HousingService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
            System.setProperty("webdriver.chrome.driver", "C:/chromedriver.exe");

            // Initialize ChromeDriver
            WebDriver driver = new ChromeDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;



            String url = "https://housing.com/in/buy/searches/P2xc23on9yrfbikhj";
            Document document = Jsoup.connect(url).get();
            Elements propertyElements = document.select("#innerApp > div.css-1io3q4n > div.css-1xg7tbs > div.css-69haxp > div.css-1m1bruh > div");
//            System.out.println(propertyElements.size());
            for (Element property : propertyElements) {
                HashMap<String, String> propertyInfo = new LinkedHashMap<>();
                String averagePrice = property.select("[id^=\"srp-\"] > div > div._mkh2mm._9s1txw._ar1bp4._fc1yb4._axkb7n._l8bsq7._vy1x30._gdnqedxx._ft8m16eo.T_47e9c049 > div._mkh2mm._1asa1q9c._rlozgrho._2hx11btx.T_6412d3ba > div._mkh2mm.T_b70307b8 > section > div.T_1e5d5ecb._e214no.T_ad2d45b4._9s1txw.T_9473b29c._5j1tlg._0h1h6o._vy18a8._be1g80._2621jn.item-container > div:nth-child(2) > div._9s1txw._ar1bp4._amkb7n._fc1h6o._r31e5h._gzftgi.T_f121fc5f > div.T_091c165f._sq1l2s._vv1q9c._ks15vq.T_efe231cd._vy1ipv._7ltvct._g3dlk8._c81fwx._cs1nn1.value").text();
                String propertyName = property.select("[id^=\"srp-\"] > div.T_cd31fb10._156v13rr._j6cmky.T_24c887cb._biqgdtch._axkb7n._9s1txw > div._mkh2mm._9s1txw._ar1bp4._fc1yb4._axkb7n._l8bsq7._vy1x30._gdnqedxx._ft8m16eo.T_47e9c049 > div._mkh2mm._1asa1q9c._rlozgrho._2hx11btx.T_6412d3ba > div._9s1txw._gqyh40._0h1q9y.T_8c8ed98f > a > h2").text();
                String propertyLocation = property.select("[id^=\"srp-\"] > div.T_cd31fb10._156v13rr._j6cmky.T_24c887cb._biqgdtch._axkb7n._9s1txw > div._mkh2mm._9s1txw._ar1bp4._fc1yb4._axkb7n._l8bsq7._vy1x30._gdnqedxx._ft8m16eo.T_47e9c049 > div._mkh2mm._1asa1q9c._rlozgrho._2hx11btx.T_6412d3ba > h3 > span").text();

                if(averagePrice.isBlank() || propertyName.isBlank())continue;

                com.housing.entities.Properties properties1 = new com.housing.entities.Properties();
                properties1.setAveragePrice(averagePrice);
                properties1.setPropertyName(propertyName);
                properties1.setPropertyLocation(propertyLocation);


                properties.add(properties1);

                updateMatch(properties1);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private void updateMatch(com.housing.entities.Properties properties1) {

        Properties properties = this.propRepo.findByPropertyName(properties1.getPropertyName()).orElse(null);
        if (properties == null) {
            this.propRepo.save(properties1);
        } else {

            properties1.setPropertyId(properties.getPropertyId());
            this.propRepo.save(properties1);
        }

    }

}
