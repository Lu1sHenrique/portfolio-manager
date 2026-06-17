package com.portfolio.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

@TestConfiguration
public class TestJacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.addMixIn(PageImpl.class, PageImplMixin.class);
        return objectMapper;
    }

    @JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable", "sort"})
    abstract static class PageImplMixin {
        @JsonProperty("content")
        abstract List<?> getContent();

        @JsonProperty("totalElements")
        abstract long getTotalElements();

        @JsonProperty("totalPages")
        abstract int getTotalPages();

        @JsonProperty("size")
        abstract int getSize();

        @JsonProperty("number")
        abstract int getNumber();

        @JsonProperty("numberOfElements")
        abstract int getNumberOfElements();

        @JsonProperty("first")
        abstract boolean isFirst();

        @JsonProperty("last")
        abstract boolean isLast();

        @JsonProperty("empty")
        abstract boolean isEmpty();
    }
}
