package com.example.MyYoutubePj.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoCreationRequest {
    String title;
    String description;
    String videoId;    // đổi từ id -> videoUrl cho khớp entity

    String thumbnail;

    Long views;
    Long likes;
    Long comments;

    String duration;
    List<String> tags;

    Timestamp publishedAt;

    ChannelRequest channel;  // Nhúng ChannelRequest vào VideoCreationRequest
}
