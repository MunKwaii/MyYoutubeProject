package com.example.MyYoutubePj.service;

import com.example.MyYoutubePj.dto.request.ChannelRequest;
import com.example.MyYoutubePj.dto.request.VideoCreationRequest;
import com.example.MyYoutubePj.dto.response.VideoMainPageResponse;
import com.example.MyYoutubePj.entity.Channel;
import com.example.MyYoutubePj.entity.Video;
import com.example.MyYoutubePj.repository.ChannelRepository;
import com.example.MyYoutubePj.repository.VideoRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class VideoService {
    final VideoRepository videoRepository;
    final ChannelRepository channelRepository;
    private static final int SEED_VIDEO_COUNT = 1000;
    private final Map<String, List<String>> seedToVideoIdsCache = new ConcurrentHashMap<>();

    @Autowired
    public VideoService(VideoRepository videoRepository, ChannelRepository channelRepository) {
        this.videoRepository = videoRepository;
        this.channelRepository = channelRepository;
    }
    // Map dữ liệu từ VideoCreationRequest sang Video entity và lưu vào DB
    public Video createVideoFromYouTubeApi(VideoCreationRequest request) {
        String videoUrl = "https://www.youtube.com/watch?v=" + request.getVideoId();
        // Kiểm tra hoặc tạo mới Channel
        Channel channel = channelRepository.findById(request.getChannel().getChannelId())
                .orElseGet(() -> {
                    Channel newChannel = Channel.builder()
                            .channelId(request.getChannel().getChannelId())
                            .channelName(request.getChannel().getChannelName())
                            .build();
                    return channelRepository.save(newChannel);
                });
        Video video = Video.builder()
                .videoUrl(videoUrl)
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .views(request.getViews())
                .likes(request.getLikes())
                .comments(request.getComments())
                .duration(request.getDuration())
                .tags(request.getTags())
                .publishedAt(request.getPublishedAt())
                .channel(channel)
                .build();

        return videoRepository.save(video);
    }
    // Cập nhật Channel Image và Channel Subscribers count cho table Channel
    public Channel updateChannelInfoFromYouTubeApi(String channelId, ChannelRequest request) {
        return channelRepository.findById(channelId)
                .map(channel -> {
                    channel.setChannelImage(request.getChannelImage());
                    channel.setChannelSubscribers(request.getChannelSubscribers());
                    return channelRepository.save(channel);
                })
                .orElseThrow(() -> new RuntimeException("Channel not found"));
    }
    // Truy xuất tất cả video
    public List<Video> getAllVideo() {
        return videoRepository.findAll();
    }
    // Truy xuất thông tin video qua video id
    public Optional<Video> getVideoById(String id) {
        return videoRepository.findById(id);
    }
    // Xóa video
    public void deleteVideo(String id) {
        videoRepository.deleteById(id);
    }
    // Kiểm video
    public boolean existsByVideoId(String videoId) {
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
        // Kiểm tra video đã tồn tại chưa
        return videoRepository.existsByVideoUrl(videoUrl);
    }
    public List<VideoMainPageResponse> getVideosBySeed(String seed, int page, int pageSize) {
        // Lấy cached 1000 videoId theo seed, nếu chưa có thì tạo mới
        List<String> videoIds = seedToVideoIdsCache.computeIfAbsent(seed, s -> {
            List<String> allVideoIds = videoRepository.findAllVideoIds();
            Random random = new Random(seed.hashCode());
            Collections.shuffle(allVideoIds, random);

            // Cắt 1000 video đầu tiên sau khi shuffle
            return allVideoIds.stream().limit(1000).toList();
        });

        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, videoIds.size());
        if (fromIndex >= videoIds.size()) return List.of();

        List<String> pagedIds = videoIds.subList(fromIndex, toIndex);
        List<Video> videos = videoRepository.findAllById(pagedIds);

        // Map id về Video giữ đúng thứ tự
        Map<String, Video> idToVideoMap = videos.stream()
                .collect(Collectors.toMap(Video::getId, v -> v));

        return pagedIds.stream()
                .map(id -> toMainPageResponse(idToVideoMap.get(id)))
                .toList();
    }

    public long countVideos() {
        return SEED_VIDEO_COUNT;
    }

    private VideoMainPageResponse toMainPageResponse(Video video) {
        return VideoMainPageResponse.builder()
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnail())
                .durationFormatted(formatDuration(video.getDuration()))
                .title(video.getTitle())
                .channelName(video.getChannel().getChannelName())
                .channelImageUrl(video.getChannel().getChannelImage())
                .viewCount(video.getViews())
                .publishedAtFormatted(formatPublishedAt(video.getPublishedAt()))
                .build();
    }

    private String formatDuration(String isoDuration) {
        // Convert ISO 8601 PT2H30M10S -> HH:mm:ss
        Duration duration = Duration.parse(isoDuration);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private String formatPublishedAt(Timestamp publishedAt) {
        return new SimpleDateFormat("yyyy-MM-dd").format(publishedAt);
    }
}
