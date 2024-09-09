package com.property.crawler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

    @Column(name = "search_object", nullable = false)
    private String searchObject;

    @Column(name = "generated_file", columnDefinition = "BYTEA")
    private byte[] file;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;


}
