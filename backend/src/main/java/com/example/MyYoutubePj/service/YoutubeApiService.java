package com.example.MyYoutubePj.service;

import com.example.MyYoutubePj.dto.request.ChannelRequest;
import com.example.MyYoutubePj.dto.request.VideoCreationRequest;
import com.example.MyYoutubePj.dto.response.YoutubeSearchResponse;

import com.example.MyYoutubePj.dto.response.YoutubeVideoDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class YoutubeApiService {

    private final RestTemplate restTemplate = new RestTemplate(); //RestTemplate để call API ngoài

    @Value("${youtube.api.key}")
    private String apiKey;

    // 1. Lấy 50 videoId trending theo category
    public List<String> getVideoIdsByTopic(String topicId, int maxResults) {
        String searchUrl = "https://www.googleapis.com/youtube/v3/search"
                + "?key=" + apiKey
                + "&type=video"
                + "&maxResults=" + maxResults
                + "&part=id"
                + "&order=viewCount"
                + "&videoCategoryId=" + topicId
                + "&fields=items/id/videoId";

        ResponseEntity<YoutubeSearchResponse> response =
                restTemplate.getForEntity(searchUrl, YoutubeSearchResponse.class);

        List<String> videoIds = new ArrayList<>();
        if (response.getBody() != null && response.getBody().getItems() != null) {
            for (YoutubeSearchResponse.Item item : response.getBody().getItems()) {
                videoIds.add(item.getId());
            }
        }
        return videoIds;
    }

    // 2. Lấy chi tiết cho tối đa 50 videoId
    public List<VideoCreationRequest> getVideosDetails(List<String> videoIds) {
        if (videoIds.isEmpty()) return Collections.emptyList();

        String ids = String.join(",", videoIds);
        String url = "https://www.googleapis.com/youtube/v3/videos"
                + "?id=" + ids
                + "&key=" + apiKey
                + "&part=snippet,statistics,contentDetails"
                + "&fields=items(id,snippet(title,description,thumbnails,channelId,channelTitle,publishedAt,tags),statistics(viewCount,likeCount,commentCount),contentDetails(duration))";

        ResponseEntity<YoutubeVideoDetailsResponse> response =
                restTemplate.getForEntity(url, YoutubeVideoDetailsResponse.class);

        List<VideoCreationRequest> requests = new ArrayList<>();
        if (response.getBody() != null && response.getBody().getItems() != null) {
            for (YoutubeVideoDetailsResponse.Item item : response.getBody().getItems()) {
                requests.add(parseVideo(item));
            }
        }
        return requests;
    }


    // Parse dữ liệu từ 1 item thành VideoCreationRequest
    private VideoCreationRequest parseVideo(YoutubeVideoDetailsResponse.Item item) {
        var snippet = item.getSnippet();
        var stats = item.getStatistics();
        var content = item.getContentDetails();

        System.out.println("Parsing video: ");
        System.out.println("- videoId: " + item.getId());
        System.out.println("- title: " + snippet.getTitle());
        System.out.println("- description: " + snippet.getDescription());
        System.out.println("- thumbnail: " + extractThumbnail(snippet.getThumbnails()));
        System.out.println("- views: " + stats.getViewCount());
        System.out.println("- likes: " + stats.getLikeCount());
        System.out.println("- comments: " + stats.getCommentCount());
        System.out.println("- duration: " + content.getDuration());
        System.out.println("- tags: " + snippet.getTags());
        System.out.println("- publishedAt: " + snippet.getPublishedAt());
        System.out.println("- channelId: " + snippet.getChannelId());
        System.out.println("- channelTitle: " + snippet.getChannelTitle());

        return VideoCreationRequest.builder()
                .videoId(item.getId())
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .thumbnail(extractThumbnail(snippet.getThumbnails()))
                .views(parseLong(stats.getViewCount()))
                .likes(parseLong(stats.getLikeCount()))
                .comments(parseLong(stats.getCommentCount()))
                .duration(content.getDuration())
                .tags(snippet.getTags() != null ? snippet.getTags() : new ArrayList<>())
                .publishedAt(parseTimestamp(snippet.getPublishedAt()))
                .channel(ChannelRequest.builder()
                        .channelId(snippet.getChannelId())
                        .channelName(snippet.getChannelTitle())
                        .build())
                .build();
    }


    private String extractThumbnail(YoutubeVideoDetailsResponse.Thumbnails thumbnails) {
        if (thumbnails.getMaxres() != null && thumbnails.getMaxres().getUrl() != null) {
            return thumbnails.getMaxres().getUrl();
        } else if (thumbnails.getStandard() != null && thumbnails.getStandard().getUrl() != null) {
            return thumbnails.getStandard().getUrl();
        } else if (thumbnails.getHigh() != null && thumbnails.getHigh().getUrl() != null) {
            return thumbnails.getHigh().getUrl();
        } else if (thumbnails.getMedium() != null && thumbnails.getMedium().getUrl() != null) {
            return thumbnails.getMedium().getUrl();
        } else if (thumbnails.getDefaultThumbnail() != null && thumbnails.getDefaultThumbnail().getUrl() != null) {
            return thumbnails.getDefaultThumbnail().getUrl();
        }
        return "";
    }

    private Timestamp parseTimestamp(String iso) {
        return Timestamp.valueOf(iso.replace("T", " ").replace("Z", ""));
    }

    private Long parseLong(Object val) {
        try {
            return Long.parseLong((String) val);
        } catch (Exception e) {
            return 0L;
        }
    }
}

