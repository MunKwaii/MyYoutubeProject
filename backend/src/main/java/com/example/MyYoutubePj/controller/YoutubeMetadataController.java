package com.example.MyYoutubePj.controller;

import com.example.MyYoutubePj.config.KeywordFileProperties;
import com.example.MyYoutubePj.scheduler.YoutubeMetadataScheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class YoutubeMetadataController {
    private final YoutubeMetadataScheduler youtubeMetadataScheduler;
    private final ResourceLoader resourceLoader;
    private final KeywordFileProperties keywordFileProperties;
    private final ObjectMapper objectMapper;
    @PostMapping("/load-keywords")
    public String loadKeywordsFromFile() {
        try {
            String filePath = keywordFileProperties.getPath();
            Resource resource = resourceLoader.getResource(filePath);
            InputStream inputStream = resource.getInputStream();
            List<String> keywords = Arrays.asList(objectMapper.readValue(inputStream, String[].class));

            youtubeMetadataScheduler.parseAndSaveKeywords(keywords);
            return "Loaded " + keywords.size() + " keywords successfully from " + filePath;
        } catch (IOException e) {
            log.error("Failed to load keywords", e);
            return "Failed to load keywords: " + e.getMessage();
        }
    }
}
