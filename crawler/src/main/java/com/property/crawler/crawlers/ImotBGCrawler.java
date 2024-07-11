package com.property.crawler.crawlers;

import com.property.crawler.enums.ActionType;
import com.property.crawler.enums.City;
import com.property.crawler.enums.PropertyType;
import com.property.crawler.property.PropertySearchDto;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImotBGCrawler extends WebCrawler {

    private final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        //https://www.imot.bg/pcgi/imot.cgi?act=17&actions=2&f1=1&f2=3&f3=%D1%EE%F4%E8%FF&f4=%D6%E5%ED%F2%FA%F0&f5=90&button=%CF+%CE+%CA+%C0+%C6+%C8&f6=2
//        https://www.imot.bg/pcgi/imot.cgi?act=5&adv=1c171636550004880
        String urlString = url.getURL().toLowerCase();
        WebURL webURL = new WebURL();
        webURL.setURL("https://www.imot.bg/pcgi/imot.cgi?act=17&actions=2&f1=1&f2=3&f3=%D1%EE%F4%E8%FF&f4=%D6%E5%ED%F2%FA%F0&f5=90&button=%CF+%CE+%CA+%C0+%C6+%C8&f6=2");
        referringPage.setWebURL(webURL);
        return !EXCLUSIONS.matcher(urlString).matches()
            && urlString.startsWith("https://www.imot.bg/pcgi/imot.cgi?act=5&adv=1c168935328507737");
    }

    @Override
    public void visit(Page page) {
        String pageUrl = page.getWebURL().getURL();


        if (page.getParseData() instanceof HtmlParseData htmlParseData && !page.getWebURL().toString().contains("paged=")) {
            String html = htmlParseData.getHtml();
            String url = String.valueOf(page.getWebURL());
        }

//        if (pageUrl.contains("imot.cgi")) {
//            try {
//                String imotBgUrl = "https://www.imot.bg/pcgi/imot.cgi";

//                String searchUrl = buildSearchPropertyUrl(imotBgUrl,
//                    new PropertySearchDto(1, 3,
//                        City.SOFIA.getCityName(), "Център", 90));
                // Construct URL with query parameters
//                URL obj = new URL(searchUrl);
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//                //act=17&actions=2&f1=1&f2=3&f3=%D1%EE%F4%E8%FF&f4=%D6%E5%ED%F2%FA%F0&f5=90&button=%CF+%CE+%CA+%C0+%C6+%C8&f6=2
//                // Set request method
//                con.setRequestMethod("GET");
//
//                // Optional default headers if needed
//                con.setRequestProperty("User-Agent", "Mozilla/5.0");
//                System.out.println("Request URL: " + obj.toString());
//
//                // Send GET request
//                int responseCode = con.getResponseCode();
//                System.out.println("Response Code: " + responseCode);
//
//                // Read the response
//                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                String inputLine;
//                StringBuilder response = new StringBuilder();
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//                // Print result
//                System.out.println(response.toString());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
            }


}
