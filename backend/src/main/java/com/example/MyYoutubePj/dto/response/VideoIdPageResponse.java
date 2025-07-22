package com.example.MyYoutubePj.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoIdPageResponse {
    private List<String> videoIds;
    private String nextPageToken;
}

