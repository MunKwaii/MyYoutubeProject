package com.example.MyYoutubePj.service;

import com.example.MyYoutubePj.dto.request.ChannelRequest;
import com.example.MyYoutubePj.dto.request.VideoCreationRequest;
import com.example.MyYoutubePj.dto.response.ChannelResponse;
import com.example.MyYoutubePj.dto.response.YoutubeVideoIdPageResponse;
import com.example.MyYoutubePj.dto.response.YoutubeSearchResponse;

import com.example.MyYoutubePj.dto.response.YoutubeVideoDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class YoutubeApiService {

    private final RestTemplate restTemplate = new RestTemplate(); //RestTemplate để call API ngoài

    @Value("${youtube.api.key}")
    private String apiKey;

    // 1. Lấy 50 videoId trending theo category
    public YoutubeVideoIdPageResponse getVideoIdsByKeyword(int maxResults, String keyword, String nextPageToken) {
        String searchUrl = "https://www.googleapis.com/youtube/v3/search"
                + "?key=" + apiKey
                + "&type=video"
                + "&maxResults=" + maxResults
                + "&part=id"
                + "&order=viewCount"
                + "&fields=items/id/videoId,nextPageToken"
                + "&q=" + keyword;

        if (nextPageToken != null && !nextPageToken.isBlank()) {
            searchUrl += "&pageToken=" + nextPageToken;
        }

        ResponseEntity<YoutubeSearchResponse> response = restTemplate.getForEntity(searchUrl, YoutubeSearchResponse.class);
        YoutubeSearchResponse body = response.getBody();

        List<String> videoIds = new ArrayList<>();
        if (body != null && body.getItems() != null) {
            for (YoutubeSearchResponse.Item item : body.getItems()) {
                videoIds.add(item.getId().getVideoId());
            }
        }
        return new YoutubeVideoIdPageResponse(videoIds, body != null ? body.getNextPageToken() : null);
    }

    // 2. Từ số lượng maxResult videoID lấy được từ API trên, tạo VideoCreationRequest từ truy vấn metadata cụ thể
    public List<VideoCreationRequest> getVideosDetails(List<String> videoIds) {
        if (videoIds.isEmpty()) return Collections.emptyList();

        String ids = String.join(",", videoIds);
        String url = "https://www.googleapis.com/youtube/v3/videos"
                + "?id=" + ids
                + "&key=" + apiKey
                + "&part=snippet,statistics,contentDetails"
                + "&fields=items(id,snippet(title,description,thumbnails,channelId,channelTitle,publishedAt,tags),statistics(viewCount,likeCount,commentCount),contentDetails(duration))";
        // Gọi API bằng restTemplate, map Json kết quả từ API sang YoutubeVideoDetailsResponse (DTO)
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

    // 3. Lấy các trường còn thiếu trong channel entity
    public ChannelRequest getChannelInfo(String channelId){
        if (channelId.isEmpty()) return null;
        String url = "https://www.googleapis.com/youtube/v3/channels"
                + "?id=" + channelId
                + "&key=" + apiKey
                + "&part=snippet,statistics"
                + "&field=items(snippet/thumbnails,statistics/subscriberCount)";
        ResponseEntity<ChannelResponse> response = restTemplate.getForEntity(url, ChannelResponse.class);
        if (response.getBody() != null && response.getBody().getItems() != null && !response.getBody().getItems().isEmpty()) { // Tránh .get(0) khi items rỗng để tránh lỗi runtime
            ChannelResponse.Item item = response.getBody().getItems().get(0);
            String imageUrl = extractThumbnail(item.getSnippet().getThumbnails());
            Long subscribers = parseLong(item.getStatistics().getSubscriberCount());
            return ChannelRequest.builder()
                    .channelId(channelId)
                    .channelName(item.getSnippet().getTitle())
                    .channelImage(imageUrl)
                    .channelSubscribers(subscribers)
                    .build();
        }
        return ChannelRequest.builder()
                .channelId(channelId)
                .channelName("")
                .channelImage("missing")
                .channelSubscribers(-1L)
                .build();
    }

    // Parse dữ liệu từ 1 item thành VideoCreationRequest
    private VideoCreationRequest parseVideo(YoutubeVideoDetailsResponse.Item item) {
        var snippet = item.getSnippet();
        var stats = item.getStatistics();
        var content = item.getContentDetails();
        //Debug thông tin video xuất ra
        log.info("Parsing video - ID: {}, Title: {}", item.getId(), (snippet != null ? snippet.getTitle() : "N/A"));
        String title = (snippet != null ? snippet.getTitle() : "");
        if (title.isBlank()) {
            log.warn("Video ID {} has empty title.", item.getId());
        }
        if (snippet != null) {
            Timestamp publishedAt = parseTimestamp(snippet.getPublishedAt());
            if (publishedAt == null) {
                log.warn("Video ID {} has invalid publishedAt: {}", item.getId(), snippet.getPublishedAt());
            }
        }
        return VideoCreationRequest.builder()
                .videoId(item.getId())
                .title(snippet != null ? snippet.getTitle() : "")
                .description(snippet != null ? snippet.getDescription() : "")
                .thumbnail(snippet != null ? extractThumbnail(snippet.getThumbnails()) : "")
                .views(stats != null ? parseLong(stats.getViewCount()) : 0L)
                .likes(stats != null ? parseLong(stats.getLikeCount()) : 0L)
                .comments(stats != null ? parseLong(stats.getCommentCount()) : 0L)
                .duration(content != null ? content.getDuration() : "")
                .tags(snippet != null && snippet.getTags() != null ? snippet.getTags() : new ArrayList<>())
                .publishedAt(snippet != null ? parseTimestamp(snippet.getPublishedAt()) : null)
                .channel(snippet != null ? ChannelRequest.builder()
                        .channelId(snippet.getChannelId())
                        .channelName(snippet.getChannelTitle())
                        .build() : null)
                .build();
    }
    // Các hàm hỗ trợ
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
    private String extractThumbnail(ChannelResponse.Thumbnails thumbnails) {
        if (thumbnails.getHigh() != null && thumbnails.getHigh().getUrl() != null) {
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
            if (val instanceof Number) {
                return ((Number) val).longValue();
            } else if (val instanceof String) {
                return Long.parseLong((String) val);
            } else {
                log.warn("Unsupported type for parsing to Long: {}", val);
                return 0L;
            }
        } catch (Exception e) {
            log.warn("Cannot parse value to Long: {}", val, e);
            return 0L;
        }
    }
}

