package com.housing.services.impl;

import com.housing.entities.Properties;
import com.housing.entities.SearchProperty;
import com.housing.repositories.PropRepo;
import com.housing.repositories.SearchPropRepo;
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
    private SearchPropRepo searchPropRepo;

    public HousingServiceImpl(PropRepo propRepo) {
        this.propRepo = propRepo;
    }
    public HousingServiceImpl(SearchPropRepo searchPropRepo) {
        this.searchPropRepo = searchPropRepo;
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

        ArrayList<String> propertyNames = new ArrayList<>();
        propertyNames.add("Smart World Orchard");
        propertyNames.add("DLF The Arbour");
        propertyNames.add("Signature Global Park 4 and 5");
        propertyNames.add("Aradhya Homes");
        propertyNames.add("Adani Samsara Vilasa");
        propertyNames.add("Signature Global City 63A");
        propertyNames.add("Silverglades The Melia");
        propertyNames.add("Conscient Hines Elevate");
        propertyNames.add("Birla Navya Anaika");
        propertyNames.add("Pyramid Urban Homes");
        propertyNames.add("Tulip Yellow");
        propertyNames.add("ROF Ambliss");
        propertyNames.add("Puri The Aravallis");
        propertyNames.add("Godrej Nature Plus");
        propertyNames.add("Godrej Serenity Gurgaon");
        propertyNames.add("Birla Navya");
        propertyNames.add("Eldeco Acclaim");
        propertyNames.add("M3M Flora 68");
        propertyNames.add("M3M Heights");
        propertyNames.add("M3M Sierra");
        propertyNames.add("M3M Natura");
        propertyNames.add("Signature Global Superbia");
        propertyNames.add("Godrej Prive");
        propertyNames.add("Godrej Habitat");
        propertyNames.add("Emaar Digi Homes");
        propertyNames.add("Godrej Air Sector 85");
        propertyNames.add("Adani Samsara");
        propertyNames.add("Ashiana The Center Court Sports Residences");
        propertyNames.add("Central Park 3 Flower Valley");
        propertyNames.add("Experion The Heart Song");
        propertyNames.add("BPTP The Pedestal");
        propertyNames.add("Adani Oyster Platinum Tower");
        propertyNames.add("DLF Alameda Independent Floors");
        propertyNames.add("DLF Independent Floors");
        propertyNames.add("Ashiana Anmol Phase 2");
        propertyNames.add("Emaar Emerald Nuevo");
        propertyNames.add("Tata La Vida Codename Gateway Select");
        propertyNames.add("Signature The Millennia 3");
        propertyNames.add("Signature Global Prime Phase 2");
        propertyNames.add("DLF Siris Estate");
        propertyNames.add("DLF Moulsari Enclave");
        propertyNames.add("M3M Golf Estate Fairway West");
        propertyNames.add("Signature Global Golf Greens");
        propertyNames.add("Signature Global The Millennia Phase 1");
        propertyNames.add("Signature Global Proxima Phase 2");
        propertyNames.add("Central Park Flower Valley Aqua Front Towers");
        propertyNames.add("Emerald Green");
        propertyNames.add("Signature The Serenas");
        propertyNames.add("Signature Global Andour Height");
        propertyNames.add("M3M Golf Estate Fairway East");
        propertyNames.add("Emaar Emerald Classic");
        propertyNames.add("Central Park Bellavista Towers");
        propertyNames.add("Signature Global Aspire");
        propertyNames.add("Signature Global Prime");
        propertyNames.add("M3M Ikonic");
        propertyNames.add("DLF Express Greens");
        propertyNames.add("Conscient Heritage Max II");
        propertyNames.add("DLF Ultima Phase II");
        propertyNames.add("Sobha International City Phase 4");
        propertyNames.add("Bestech Park View Ananda Exclusive Villas");
        propertyNames.add("Godrej Summit Phase IV");
        propertyNames.add("Emaar MGF Ekaantam");
        propertyNames.add("Ansal East West Apartment");
        propertyNames.add("Emaar The Palm Spring Villas");
        propertyNames.add("Emaar MGF Marvel");
        propertyNames.add("Godrej Summit Phase II");
        propertyNames.add("Signature Solera Apartment");
        propertyNames.add("Emaar MGF The Palm Drive Studios");
        propertyNames.add("Ansal Florence Residency");
        propertyNames.add("M3M ST Andrews");
        propertyNames.add("M3M Milano");
        propertyNames.add("Emaar MGF The Palm Drive Villas");
        propertyNames.add("Emaar MGF Garden Terraces");
        propertyNames.add("M3M Sky City");
        propertyNames.add("M3M Duo High");
        propertyNames.add("M3M Sky Lofts");
        propertyNames.add("Signature Roselia Phase 2");
        propertyNames.add("Adani Oyster Grande Phase 2");
        propertyNames.add("Ashiana Housing Anmol");
        propertyNames.add("Bestech Park View Residency");
        propertyNames.add("Bestech Park View City 2");
        propertyNames.add("DLF New Town Heights III");
        propertyNames.add("Mapsko Casa Bella-Villas");
        propertyNames.add("Bestech Park View Grand Spa-Spa Signature Tower");
        propertyNames.add("Conscient Heritage One");
        propertyNames.add("Emaar Palm Terraces Select");
        propertyNames.add("Bestech Park View Spa Next");
        propertyNames.add("Central Park II-Belgravia Resort Residences");
        propertyNames.add("Eldeco Accolade");
        propertyNames.add("Hero The Ark");
        propertyNames.add("Ganga Tathastu");
        propertyNames.add("ATS Homekraft Bonheur Avenue");
        propertyNames.add("DLF Royale Residences");
        propertyNames.add("Ashiana Amarah");
        propertyNames.add("M3M My Den");
        propertyNames.add("Signature Global Imperial");
        propertyNames.add("Royal Green Heights");
        propertyNames.add("Signature The Millennia 4");
        propertyNames.add("Ashiana Anmol Phase 3");
        propertyNames.add("Pyramid Infinity");
        propertyNames.add("Pyramid Nest");
        propertyNames.add("Ace Palm Floors");
        propertyNames.add("Pyramid Altia");
        propertyNames.add("Sobha City Chintels Metropolis");
        propertyNames.add("Pyramid Midtown");
        propertyNames.add("Supertech Montana View");
        propertyNames.add("Supertech Hill Crest Floors");
        propertyNames.add("Pyramid Fusion Homes");
        propertyNames.add("CHD Y Suites");
        propertyNames.add("M3M Trump Tower");
        propertyNames.add("Breez Global Hill View");
        propertyNames.add("Indiabulls Gulmohar Avenue");
        propertyNames.add("Sobha City Gurgaon");
        propertyNames.add("Supertech Scarlet Suits");
        propertyNames.add("M3M ST. Andrews Golf Residences");
        propertyNames.add("Godrej Premia Tower");
        propertyNames.add("Supertech Azaliya");
        propertyNames.add("Silverglades The Melia First Citizen");
        propertyNames.add("M3M Marina");
        propertyNames.add("Mahindra Luminare");
        propertyNames.add("M3M Latitude");
        propertyNames.add("M3M One Key Resiments");
        propertyNames.add("Tata Raheja Arabella");
        propertyNames.add("CHD Resortico");
        propertyNames.add("Sobha International City Phase 3");
        propertyNames.add("Emaar The Palm Drive-Palm Studios");
        propertyNames.add("ASF Insignia Isle-De-Royale Residences");
        propertyNames.add("Sobha International City Phase 2");
        propertyNames.add("Sobha International City Phase 1");
        propertyNames.add("Ansal Highland Park");
        propertyNames.add("Tata Primanti-Tower Residences");
        propertyNames.add("Tata Primanti-Executive Apartments");
        propertyNames.add("Tata Primanti-Executive Floors");
        propertyNames.add("Tata Primanti Villas");
        propertyNames.add("AIPL The Peaceful Homes");
        propertyNames.add("Uppals Canary Homes");
        propertyNames.add("3C Orris Greenopolis");
        propertyNames.add("Emaar Palm Terraces");
        propertyNames.add("Emaar Emerald Floors");
        propertyNames.add("M3M Polo Suites");
        propertyNames.add("Tata Raheja Raisina Residency");
        propertyNames.add("Emaar The Enclave");
        propertyNames.add("Emaar The Palm Drive-The Sky Terraces");
        propertyNames.add("M3M Golf Estate");
        propertyNames.add("Emaar Marbella");
        propertyNames.add("Ireo The Grand Arch");
        propertyNames.add("Emaar Emerald Floors Select");
        propertyNames.add("Emaar Emerald Floors Premier");
        propertyNames.add("M3M Merlin");
        propertyNames.add("Emaar The Palm Drive-The Premier Terraces");
        propertyNames.add("Emaar Urban Oasis");
        propertyNames.add("MRG Primark");
        propertyNames.add("Pareena Hanu Residency");
        propertyNames.add("BST Green Bhoomi");
        propertyNames.add("Rajvik Greens");
        propertyNames.add("Mapsko The Icon 79");
        propertyNames.add("Landmark Avana");
        propertyNames.add("M3M Golf Estate 2");
        propertyNames.add("Meffier Golden Park");
        propertyNames.add("GLS Central Avenue");
        propertyNames.add("Zara Roma");
        propertyNames.add("Yashika 104");
        propertyNames.add("Riseonic Solitaire");
        propertyNames.add("MVN Athens Sohna");
        propertyNames.add("Breez Global Heights 88");
        propertyNames.add("SHF Homes");
        propertyNames.add("True Habitat Bodh");
        propertyNames.add("JMS The Nation");
        propertyNames.add("Breez Global Heights 89");
        propertyNames.add("Conscient Habitat Prime");
        propertyNames.add("Viridian Plaza 106 Phase 2");
        propertyNames.add("Conscient Habitat 102");
        propertyNames.add("ROF Insignia Park");
        propertyNames.add("GLS Avenue 86");
        propertyNames.add("Mahira Homes 104");
        propertyNames.add("Suncity Vatsal Valley");
        propertyNames.add("Krrish Monde De Provence Phase 2");
        propertyNames.add("Stanford Amaara Residences");
        propertyNames.add("Tulsiani Easy In Homes");
        propertyNames.add("Santur Aspira");
        propertyNames.add("RBC Royal Homes");
        propertyNames.add("Breez Flora Avenue 33");
        propertyNames.add("Allure Estate");
        propertyNames.add("MRG Skyline");
        propertyNames.add("MKS County");
        propertyNames.add("Suncity Avenue 76");
        propertyNames.add("IRWO Classic Apartment");
        propertyNames.add("Shree Ganesh Apartments Gurgaon");
        propertyNames.add("ROF Atulyas");
        propertyNames.add("Pyramid Urban Homes Phase 2 Extension");
        propertyNames.add("GLS South Avenue");
        propertyNames.add("OSB Venetian");
        propertyNames.add("Sidhartha Diplomats Golf Link");
        propertyNames.add("Maxworth Aashray");
        propertyNames.add("Mahira Homes 95");
        propertyNames.add("Mahira Homes 103");
        propertyNames.add("Capital Heights");
        propertyNames.add("Czar Mahira Homes 63A");
        propertyNames.add("Pareena Rama Homes");
        propertyNames.add("GLS Arawali Homes Phase 2");
        propertyNames.add("Tulip Leaf");
        propertyNames.add("Raheja Akshara");
        propertyNames.add("KLJ Square");
        propertyNames.add("Lotus Affordable Housing");
        propertyNames.add("Landmark The Homes 81");
        propertyNames.add("KST Infrastructure Urban Universe");
        propertyNames.add("Ocean Seven Expressway Towers");
        propertyNames.add("Aviation Heights");
        propertyNames.add("Burman GSC Spectrum Centre");
        propertyNames.add("BPTP Park Serene Phase II");
        propertyNames.add("Satguru Apartments");
        propertyNames.add("Paras Square Service Apartments");
        propertyNames.add("V Square Oodles Skywalk");
        propertyNames.add("Hero Homes Phase 2");
        propertyNames.add("ROF Alante");
        propertyNames.add("SSG Yash Apartment 3");
        propertyNames.add("Unitech Woodstock Floors");
        propertyNames.add("ERA Cosmo City Phase I");
        propertyNames.add("The Center Court");
        propertyNames.add("Imperia Rubix");
        propertyNames.add("SS Hibiscus 2");
        propertyNames.add("BPTP Fortune Towers");
        propertyNames.add("Satya Element One Service Apartment");
        propertyNames.add("Satya The Hermitage Phase 2");
        propertyNames.add("Tulip Lemon");
        propertyNames.add("Unitech Uniworld Gardens 2");
        propertyNames.add("Era 103");
        propertyNames.add("Earth Elacasa");
        propertyNames.add("Ompee KS Residency");
        propertyNames.add("Unitech Espace Nirvana Country");
        propertyNames.add("Unitech Sunbreeze");
        propertyNames.add("Kohli One Malibu Town");
        propertyNames.add("Ansal Celebrity Suites");
        propertyNames.add("Orris Kohana Villas");
        propertyNames.add("Raheja Atlantis II");
        propertyNames.add("MVN Athens Phase II");
        propertyNames.add("Breez Global Heights");
        propertyNames.add("K World Royal Court");
        propertyNames.add("AVL 36 Gurgaon");
        propertyNames.add("Maxworth Premier Urban");
        propertyNames.add("Unitech Anthea Floors");
        propertyNames.add("Earth Copia");
        propertyNames.add("Ansal Lemon Grove");
        propertyNames.add("Golden Damas Studio Apartment");
        propertyNames.add("Sare Crescent Parc");
        propertyNames.add("Era Royal Ville");
        propertyNames.add("Vipul World Floors");
        propertyNames.add("Halwasiya Jalvayu Vihar");
        propertyNames.add("Era Cosmo City Phase 2");
        propertyNames.add("Forte Point The Oliver Spire");
        propertyNames.add("Kshitij Ramsons");
        propertyNames.add("Pratham Meghdoot Apartment");
        propertyNames.add("Satya Platina");
        propertyNames.add("Era Cosmo City Phase 3");
        propertyNames.add("Soldier Officer Heights");
        propertyNames.add("Zara Aavaas");
        propertyNames.add("OSB Golf Heights");
        propertyNames.add("Mahira Homes");
        propertyNames.add("AIPL Club Residences");
        propertyNames.add("Vatika The Turning Point");
        propertyNames.add("Ascott Ireo City");
        propertyNames.add("Raheja Maheshwara");
        propertyNames.add("ILD GSR Drive");
        propertyNames.add("Ashiana Mulberry");
        propertyNames.add("AIPL Zen Residences");
        propertyNames.add("SS New Luxury Floors");
        propertyNames.add("Orris The Blue Lagoon");
        propertyNames.add("Satya Residences");
        propertyNames.add("Nimai Familia");
        propertyNames.add("Homestead India Cuteburrow Residences");
        propertyNames.add("Lotus Greens Arascape");
        propertyNames.add("Shree Vardhman Olive");
        propertyNames.add("Raheja Krishna Affordable Housing");
        propertyNames.add("The City Of Homestead");
        propertyNames.add("HCBS Sports Ville");
        propertyNames.add("Supertech Hill Town");
        propertyNames.add("Vatika Seven Seasons");
        propertyNames.add("Cosmos Express 99");
        propertyNames.add("Chintels Acropolis");
        propertyNames.add("Supertech 76 Canvas");
        propertyNames.add("Gold Souk Golf Links");
        propertyNames.add("V Square Group Housing Project");
        propertyNames.add("ILD Arete");
        propertyNames.add("Ambience Creacions");
        propertyNames.add("Ansal Esencia - Amara Villas");
        propertyNames.add("Versalia WoodWinds");
        propertyNames.add("Ansal Esencia - Sovereign Floors");
        propertyNames.add("Vatika One Express City");
        propertyNames.add("Vatika Boulevard Residences");
        propertyNames.add("Capital Residency 360");
        propertyNames.add("Corona Graceiux");
        propertyNames.add("Today Canary Greens");
        propertyNames.add("CHD Vann");
        propertyNames.add("Supertech Hues");
        propertyNames.add("Paras Quartier");
        propertyNames.add("NBCC Green View");
        propertyNames.add("ITC Garden Estate");
        propertyNames.add("Unitech Aspen Greens");
        propertyNames.add("Woodview Residences");
        propertyNames.add("BPTP Visionnaire Villas");
        propertyNames.add("ILD Engracia");
        propertyNames.add("SS Delight and Splendours");
        propertyNames.add("SS The Lilac");
        propertyNames.add("Tulip Violet");
        propertyNames.add("Ansal API Versalia");
        propertyNames.add("Spire Orion at South");
        propertyNames.add("Spire South");
        propertyNames.add("Sidhartha NCR One");
        propertyNames.add("Ansal Celebrity Homes");
        propertyNames.add("Today Blossoms II");
        propertyNames.add("Unitech Rakshak");
        propertyNames.add("Ardee City The Residency");
        propertyNames.add("Supertech 48 Canvas");
        propertyNames.add("Unitech Uniworld City");
        propertyNames.add("Spire Woods");
        propertyNames.add("Krrish The Eiffel");
        propertyNames.add("Ambience Island Lagoon");
        propertyNames.add("Krrish Provence Estate");
        propertyNames.add("Krrish Monde De Provence");
        propertyNames.add("Ansal Valley View Estate");
        propertyNames.add("Ansal Esencia-Mulberry Homes");
        propertyNames.add("Ireo Gurgaon Hills");
        propertyNames.add("Ireo The Corridors");
        propertyNames.add("M2K Beau Monde");
        propertyNames.add("SS Aaron Ville");
        propertyNames.add("Today Blossoms I");
        propertyNames.add("Unitech South City II");
        propertyNames.add("SS The Palladians");
        propertyNames.add("M2K Aura");
        propertyNames.add("Ramprastha City The Edge Towers");
        propertyNames.add("Ramprastha City The View");
        propertyNames.add("Ramprastha City The Atrium");
        propertyNames.add("BPTP Spacio Park Serene");
        propertyNames.add("BPTP Park Generations");
        propertyNames.add("Ramprastha City Rise");
        propertyNames.add("BPTP Chateau Villa");
        propertyNames.add("Eminence Kimberly Suites");
        propertyNames.add("Raheja Shilas Independent Floors");
        propertyNames.add("Aakriti Vastus");
        propertyNames.add("GPL Mint Tower");
        propertyNames.add("Sidhartha Estella");
        propertyNames.add("Agrante Beethovens 8");
        propertyNames.add("Today Royal Elegancia");
        propertyNames.add("Krrish Florence Estate");
        propertyNames.add("Homestead Ballet By Sharapova");
        propertyNames.add("Unitech Vistas Gurgaon");
        propertyNames.add("Unitech South Park Gurgaon");
        propertyNames.add("Tulip Ivory");
        propertyNames.add("CHD Avenue 71");
        propertyNames.add("Unitech Exquisite");
        propertyNames.add("Today Callidora");
        propertyNames.add("Tulip Orange");
        propertyNames.add("Omaxe The Nile");
        propertyNames.add("Shree Vardhman Victoria");
        propertyNames.add("GPL Eden Heights");
        propertyNames.add("Tulip White");
        propertyNames.add("Paras Irene");
        propertyNames.add("SARE Crescent Parc Green Parc");
        propertyNames.add("SARE Crescent Parc Royal Greens Phase II");
        propertyNames.add("Ansal API The Fernhill");
        propertyNames.add("SARE Crescent Parc Royal Greens Phase I");
        propertyNames.add("Sare Club Terraces");
        propertyNames.add("SARE Petioles");
        propertyNames.add("Prism Portico");
        propertyNames.add("Sidhartha NCR Lotus");
        propertyNames.add("Sidhartha NCR Green");
        propertyNames.add("Parkwood Westend");
        propertyNames.add("Ansal Heights Gurgaon");
        propertyNames.add("Umang Monsoon Breeze Phase I");
        propertyNames.add("Umang Monsoon Breeze Phase II");
        propertyNames.add("Vatika Seven Lamps");
        propertyNames.add("Vatika India Next Floors");
        propertyNames.add("Raheja Revanta Tapas Townhouse");
        propertyNames.add("Raheja Revanta Surya Tower");
        propertyNames.add("SS Hibiscus");
        propertyNames.add("Vipul Lavanya");
        propertyNames.add("Ansal Height 86");
        propertyNames.add("Vatika City-Sovereign");
        propertyNames.add("Eros Rosewood Villas");
        propertyNames.add("Eros Wembley Estate");
        propertyNames.add("Unitech Harmony");
        propertyNames.add("Unitech Escape");
        propertyNames.add("Anant Raj Estate The Villas");
        propertyNames.add("Anant Raj The Estate Floors");
        propertyNames.add("Pioneer Park Presidia");
        propertyNames.add("Pioneer Park Phase 1");
        propertyNames.add("BPTP Mansions Park Prime");
        propertyNames.add("Pioneer Park Araya");
        propertyNames.add("Ireo Skyon");
        propertyNames.add("Dhoot Time Residency");
        propertyNames.add("Clarion The Legend");
        propertyNames.add("Unitech The Close South");
        propertyNames.add("Unitech The Close North");
        propertyNames.add("Ireo Uptown");
        propertyNames.add("Unitech Fresco");

        // Loop through property names
        for (String propertyName : propertyNames) {
            driver.get("https://housing.com/");
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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#innerApp > div > div> div > div > div > div > div> div > h1")));


            // Parse search results
            List<WebElement> priceElements = driver.findElements(By.cssSelector("#innerApp > div > div > div > div > div > div > div > span.css-124qey8"));
            for (WebElement priceElement : priceElements) {

                SearchProperty searchProperty = SearchProperty.builder()
                        .propertyName(propertyName)
                        .averagePrice(priceElement.getText())
                        .build();

                searchPropRepo.save(searchProperty);
            }
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
