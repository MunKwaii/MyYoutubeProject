package com.example.MyYoutubePj.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class ChannelResponse {
    private List<Item> items;
    @Data
    public static class Item{
        private Snippet snippet;
        private Statistics statistics;
    }
    @Data
    public static class Snippet{
        private String title;
        private Thumbnails thumbnails;
    }
    @Data
    public static class Thumbnails{
        @JsonProperty("default")
        private Thumbnail defaultThumbnail;
        private Thumbnails.Thumbnail medium;
        private Thumbnails.Thumbnail high;
        @Data
        public static class Thumbnail {
            private String url;
        }
    }
    @Data
    public static class Statistics{
        private String subscriberCount;
    }
}
