package com.example.MyYoutubePj.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keywords.file")
@Getter
@Setter
public class KeywordFileProperties {
    private String path;
}
