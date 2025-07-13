package com.example.MyYoutubePj.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class YoutubeSearchResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
    }
}

