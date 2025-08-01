package com.example.MyYoutubePj.dto.response;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class VideoPlayPageResponse {
    String videoId;
    String videoUrl;
    String title;
    String description;
    String thumbnail;
    long views;
    long likes;
    long comments;
    String durationFormatted; // giữ nguyên định dạng ISO hoặc custom nếu muốn
    String publishedAtFormatted;
    List<String> tags;
    // Channel info
    String channelId;
    String channelName;
    String channelImage;
    Long channelSubscribers;
}

