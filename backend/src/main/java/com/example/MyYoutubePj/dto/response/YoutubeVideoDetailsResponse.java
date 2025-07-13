package com.example.MyYoutubePj.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class YoutubeVideoDetailsResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private Snippet snippet;
        private Statistics statistics;
        private ContentDetails contentDetails;
    }

    @Data
    public static class Snippet {
        private String title;
        private String description;
        private Thumbnails thumbnails;
        private String channelId;
        private String channelTitle;
        private String publishedAt;
        private List<String> tags;
    }

    @Data
    public static class Thumbnails {
        @JsonProperty("default")
        private Thumbnail defaultThumbnail;
        private Thumbnail medium;
        private Thumbnail high;
        private Thumbnail standard;
        private Thumbnail maxres;

        @Data
        public static class Thumbnail {
            private String url;
        }
    }

    @Data
    public static class Statistics {
        private String viewCount;
        private String likeCount;
        private String commentCount;
    }

    @Data
    public static class ContentDetails {
        private String duration;
    }
}

