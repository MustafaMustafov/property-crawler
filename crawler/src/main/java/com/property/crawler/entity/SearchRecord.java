package com.property.crawler.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "search_query_object")
public class SearchRecord {
    @Id
    @SequenceGenerator(name = "searchQueryObjectSequence", sequenceName = "search_query_object_id_sequence", allocationSize = 1)
    @GeneratedValue(generator = "searchQueryObjectSequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "search_object", nullable = false, columnDefinition = "TEXT")
    private String searchObject;

    @Column(name = "generated_file", columnDefinition = "BYTEA")
    private byte[] file;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public String getFormattedTimestamp() {
        return this.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getFormattedDate() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getFormattedTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
