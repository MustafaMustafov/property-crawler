package com.property.crawler.crawlers;

import com.property.crawler.enums.City;
import com.property.crawler.property.PropertySearchDto;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;

public class CrawlerRunner {

    public static void main(String[] args) throws Exception {
        File crawlStorage = new File("src/test/resources/crawler4j");
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorage.getAbsolutePath());
        config.setUserAgentString(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
        config.setMaxDepthOfCrawling(1);

        int numCrawlers = 2;

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        String searchUrl = "https://www.imot.bg/pcgi/imot.cgi";
        try {
            searchUrl = buildSearchPropertyUrl(searchUrl,
                new PropertySearchDto(1, 3,
                    City.SOFIA.getCityName(), "Център", 90));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        controller.addSeed(searchUrl);
        CrawlController.WebCrawlerFactory<ImotBGCrawler> factory = ImotBGCrawler::new;

        controller.start(factory, numCrawlers);
    }

    private static String buildSearchPropertyUrl(String url, PropertySearchDto propertySearchDto)
        throws UnsupportedEncodingException {
        return url + "?" + propertySearchDto.toQueryString();
    }
}

