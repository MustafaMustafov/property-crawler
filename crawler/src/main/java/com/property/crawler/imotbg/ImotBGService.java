package com.property.crawler.imotbg;

import com.property.crawler.enums.City;
import com.property.crawler.enums.ConstructionType;
import com.property.crawler.enums.PropertyType;
import com.property.crawler.property.Property;
import com.property.crawler.property.PropertyDto;
import com.property.crawler.property.PropertySearchDto;
import com.property.crawler.property.mapper.PropertyMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ImotBGService {

//    public List<PropertyDto> getProperty(int actionTypeId, int propertyTypeId, String city, String location,
//        int propertySize) {
//        List<PropertyDto> propertyList = new ArrayList<>();
//        try {
//            String imotBgUrl = "https://www.imot.bg/pcgi/imot.cgi";
//
//            String searchUrl = buildSearchPropertyUrl(imotBgUrl,
//                new PropertySearchDto(actionTypeId, propertyTypeId, City.getByCityName(city), location, propertySize));
//
//            URL obj = new URL(searchUrl);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//            con.setRequestMethod("GET");
//
////            con.setRequestProperty("User-Agent", "Mozilla/5.0");
//
//            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
//            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//            con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
//            con.setRequestProperty("Connection", "keep-alive");
//
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String inputLine;
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//            Set<String> hrefs = extractHrefs(response.toString());
//            propertyList = getPropertyInformations(hrefs, propertyTypeId);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return propertyList;
//    }

    public List<PropertyDto> getProperty(int actionTypeId, int propertyTypeId, String city, String location, int propertySize) {
        List<PropertyDto> propertyList = new ArrayList<>();

        try {
            String imotBgUrl = "https://www.imot.bg/pcgi/imot.cgi";
            String searchUrl = buildSearchPropertyUrl(imotBgUrl,
                new PropertySearchDto(actionTypeId, propertyTypeId, City.getByCityName(city), location, propertySize));

            URL obj = new URL(searchUrl);

            // Set up a proxy (example proxy settings)
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("195.201.126.184", 80));

            HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);
            con.setRequestMethod("GET");

            // Set User-Agent and other headers
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Pragma", "no-cache");

            // Handle cookies
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            java.net.CookieHandler.setDefault(cookieManager);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Set<String> hrefs = extractHrefs(response.toString());
                propertyList = getPropertyInformations(hrefs, propertyTypeId);
            } else {
                System.out.println("GET request not worked. Response Code: " + responseCode);
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Error response: " + response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertyList;
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


    public static List<PropertyDto> getPropertyInformations(Set<String> urls, int propertyTypeId) {
        List<Property> propertyList = new ArrayList<>();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("195.201.126.184", 80)); // Replace with a working proxy

        urls.forEach(url -> {
            try {
                Property property = new Property();
                String urlToVisit = "https:" + url;

                // Set up Jsoup connection with proxy and user-agent
                Connection connection = Jsoup.connect(urlToVisit)
                    .proxy(proxy)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .timeout(10000) // 10 seconds timeout
                    .followRedirects(true);

                Document doc = connection.get();
                property.setPropertyType(PropertyType.getById(propertyTypeId));

                extractTitle(doc, property);
                extractPublicationDateTime(doc, property);
                extractLocation(doc, property);
                extractPrice(doc, property);
                extractPricePerSqM(doc, property);
                extractDetails(doc, property);
                extractDescription(doc, property);
                property.setPropertyUrl(urlToVisit);

                propertyList.add(property);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return PropertyMapper.toDtos(propertyList);
    }

    public static void extractPublicationDateTime(Document doc, Property property) {
        Element infoElement = doc.select("div.info > div:contains(Публикувана)").first();
        boolean isUpdated = false;
        if (infoElement == null) {
            infoElement = doc.select("div.info > div:contains(Коригирана)").first();
            isUpdated = true;
        }
        if (infoElement != null) {
            Pattern dateTimePattern;
            String infoText = infoElement.text();
            if (!isUpdated) {
                dateTimePattern = Pattern.compile(
                    "Публикувана в (\\d{2}:\\d{2}) на (\\d{2}) (\\p{IsCyrillic}+), (\\d{4}) год.");
            } else {
                dateTimePattern = Pattern.compile(
                    "Коригирана в (\\d{2}:\\d{2}) на (\\d{2}) (\\p{IsCyrillic}+), (\\d{4}) год.");
            }
            Matcher matcher = dateTimePattern.matcher(infoText);

            if (matcher.find()) {
                String timeStr = matcher.group(1);
                String dayStr = matcher.group(2);
                String monthStr = matcher.group(3);
                String yearStr = matcher.group(4);

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime time = LocalTime.parse(timeStr, timeFormatter);

                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", new Locale("bg", "BG"));
                DateTimeFormatter monthParser = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
                String monthEnglish = monthParser.format(monthFormatter.parse(monthStr));

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
                LocalDate date = LocalDate.parse(dayStr + " " + monthEnglish + " " + yearStr, dateFormatter);

                LocalDateTime dateTime = LocalDateTime.of(date, time);
                property.setPublicationDateAndTime(dateTime);
            }
        }
    }

    private static void extractTitle(Document doc, Property property) {
        Element titleElement = doc.select("div h1").first();
        if (titleElement != null) {
            String title = titleElement.text();
            property.setTitle(title);
        }
    }

    private static void extractLocation(Document doc, Property property) {
        Element locationElement = doc.select("div.location").first();
        if (locationElement != null) {
            String location = locationElement.text();
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
            property.setPrice(price.trim());
        }
    }

    private static void extractPricePerSqM(Document doc, Property property) {
        Element pricePerSqMElement = doc.select("div.price span#cenakv").first();
        if (pricePerSqMElement != null) {
            String pricePerSqM = pricePerSqMElement.text();
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
            }
        }
    }

    private static void extractDescription(Document doc, Property property) {
        Element descriptionElement = doc.select("div#description_div").first();
        if (descriptionElement != null) {
            String description = descriptionElement.text();
            property.setDescription(description.trim());
        }
    }


}
