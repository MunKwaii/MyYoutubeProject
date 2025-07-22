package com.example.MyYoutubePj.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class YoutubeSearchResponse {
    private String nextPageToken;
    private List<Item> items;

    @Data
    public static class Item {
        private Id id;

        @Data
        public static class Id {
            private String videoId;
        }
    }
}

