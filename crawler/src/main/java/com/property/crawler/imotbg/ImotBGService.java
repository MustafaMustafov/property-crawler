package com.property.crawler.imotbg;

import com.property.crawler.enums.City;
import com.property.crawler.enums.ConstructionType;
import com.property.crawler.enums.PropertyType;
import com.property.crawler.property.Property;
import com.property.crawler.property.PropertySearchDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ImotBGService {

    public void getProperty(int actionTypeId, int propertyTypeId, String city, String location, int propertySize) {
        try {
            String imotBgUrl = "https://www.imot.bg/pcgi/imot.cgi";

            String searchUrl = buildSearchPropertyUrl(imotBgUrl,
                new PropertySearchDto(actionTypeId, propertyTypeId, City.getByCityName(city), location, propertySize));

            URL obj = new URL(searchUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            System.out.println("Request URL: " + obj);

            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Set<String> hrefs = extractHrefs(response.toString());
            System.out.println(getPropertyInformations(hrefs, propertyTypeId));
            hrefs.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> extractHrefs(String html) {
        Set<String> hrefs = new HashSet<>();
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");

        for (Element link : links) {
            String href = link.attr("href");
            if (href.contains("act=5&adv=")) {
                hrefs.add(href);
                if (hrefs.size() == 3) {
                    break;
                }
            }
        }
        return hrefs;
    }


    private static String buildSearchPropertyUrl(String url, PropertySearchDto propertySearchDto)
        throws UnsupportedEncodingException {
        return url + "?" + propertySearchDto.toQueryString();
    }


    public static List<Property> getPropertyInformations(Set<String> urls, int propertTypeId) {
        List<Property> propertyList = new ArrayList<>();
        urls.forEach(url -> {
            try {
                Property property = new Property();
                Document doc = Jsoup.connect("https:" + url).get();
                property.setPropertyType(PropertyType.getById(propertTypeId));

                extractTitle(doc, property);
                extractLocation(doc, property);
                extractPrice(doc, property);
                extractPricePerSqM(doc, property);
                extractDetails(doc, property);
                extractDescription(doc, property);

                propertyList.add(property);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return propertyList;
    }

    private static void extractTitle(Document doc, Property property) {
        Element titleElement = doc.select("div h1").first();
        if (titleElement != null) {
            String title = titleElement.text();
            property.setTitle(title);
            System.out.println("Title: " + title);
        }
    }

    private static void extractLocation(Document doc, Property property) {
        Element locationElement = doc.select("div.location").first();
        if (locationElement != null) {
            String location = locationElement.text();
            System.out.println("Location: " + location);
            String[] propertyLocation = location.split(",");
            if (propertyLocation.length > 0) {
                property.setCity(propertyLocation[0].trim());
            }
            if (propertyLocation.length > 1) {
                property.setLocation(propertyLocation[1].trim());
            }
        }
    }

    private static void extractPrice(Document doc, Property property) {
        Element priceElement = doc.select("div.price div#cena").first();
        if (priceElement != null) {
            String price = priceElement.text();
            System.out.println("Price: " + price);
            property.setPrice(price.trim());
        }
    }

    private static void extractPricePerSqM(Document doc, Property property) {
        Element pricePerSqMElement = doc.select("div.price span#cenakv").first();
        if (pricePerSqMElement != null) {
            String pricePerSqM = pricePerSqMElement.text();
            System.out.println("Price per square meter: " + pricePerSqM);
            property.setPricePerSqM(pricePerSqM.trim());
        }
    }

    private static void extractDetails(Document doc, Property property) {
        Elements details = doc.select("div.adParams div");
        for (Element detail : details) {
            String detailText = detail.text();
            Element strongElement = detail.select("strong").first();
            if (strongElement != null) {
                String detailValue = strongElement.text();

                if (detailText.contains("Площ")) {
                    String[] totalSizeStr = detailValue.split(" ");
                    try {
                        double totalSize = Double.parseDouble(totalSizeStr[0]);
                        property.setTotalSize(totalSize);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (detailText.contains("Етаж")) {
                    property.setFloorNumber(detailValue.trim());
                } else if (detailText.contains("Строителство")) {
                    property.setConstructionType(ConstructionType.getConstructionTypeByValue(detailValue.trim()));
                }

                System.out.println(detail.text());
            }
        }
    }

    private static void extractDescription(Document doc, Property property) {
        Element descriptionElement = doc.select("div#description_div").first();
        if (descriptionElement != null) {
            String description = descriptionElement.text();
            System.out.println("Description: " + description);
            property.setDescription(description.trim());
        }
    }


}
