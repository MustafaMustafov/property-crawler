package com.property.crawler.repository;

import com.property.crawler.entity.SearchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRecordRepository extends JpaRepository<SearchRecord, Long> {

}
