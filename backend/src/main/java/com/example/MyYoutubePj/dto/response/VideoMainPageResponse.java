package com.example.MyYoutubePj.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoMainPageResponse {
    String videoUrl;
    String thumbnailUrl;
    String durationFormatted;
    String title;
    String channelImageUrl;
    String channelName;
    Long viewCount;
    String publishedAtFormatted;
}
