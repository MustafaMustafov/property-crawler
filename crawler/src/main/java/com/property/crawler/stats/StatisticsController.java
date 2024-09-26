package com.property.crawler.stats;

import com.property.crawler.entity.SearchRecord;
import com.property.crawler.repository.SearchRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatisticsController {
    private final SearchRecordRepository searchRecordRepository;

    @GetMapping("/search-records")
    public String getSearchRecords(Model model) {
        List<SearchRecord> records = searchRecordRepository.findAll();
        model.addAttribute("records", records);
        return "search-records";
    }
}
