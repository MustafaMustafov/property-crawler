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
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ImotBGService {

    private static final String PROXY_TUNNEL = "93.152.208.207";
    private static final int PORT = 50100;

    public List<PropertyDto> getProperty(int actionTypeId, int propertyTypeId, String city, String location,
        int propertySize) {
        List<PropertyDto> propertyList = new ArrayList<>();
        String imotBgUrl = "https://www.imot.bg/pcgi/imot.cgi";

        try {
            String searchUrl = buildSearchPropertyUrl(imotBgUrl,
                new PropertySearchDto(actionTypeId, propertyTypeId, City.getByCityName(city), location, propertySize));

            HttpHost proxy = new HttpHost("93.152.208.207", 50100, "http");
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                new AuthScope(proxy.getHostName(), proxy.getPort()),
                new UsernamePasswordCredentials("mmustafovjob", "N3uyNbHydZ")
            );

            CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setProxy(proxy)
                .build();

            HttpGet httpGet = new HttpGet(searchUrl);
            CloseableHttpResponse response = httpClient.execute(httpGet);

            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    StringBuilder responseContent = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        responseContent.append(inputLine);
                    }
                    in.close();

                    Set<String> hrefs = extractHrefs(responseContent.toString());
                    propertyList = getPropertyInformations(hrefs, propertyTypeId);
                }
            } finally {
                response.close();
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

        HttpHost proxy = new HttpHost(PROXY_TUNNEL, PORT, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(PROXY_TUNNEL, PORT),
            new UsernamePasswordCredentials("mmustafovjob", "N3uyNbHydZ"));

        try (CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultCredentialsProvider(credsProvider)
            .setProxy(proxy)
            .build()) {

            for (String url : urls) {
                try {
                    Property property = new Property();
                    String urlToVisit = "https:" + url;

                    HttpGet httpGet = new HttpGet(urlToVisit);
                    httpGet.setHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

                    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            // Handle response encoding
                            String content = EntityUtils.toString(entity, "windows-1251");
                            Document doc = Jsoup.parse(content, "windows-1251");

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
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
