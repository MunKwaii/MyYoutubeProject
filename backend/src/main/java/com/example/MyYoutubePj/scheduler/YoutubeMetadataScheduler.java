package com.example.MyYoutubePj.scheduler;

import com.example.MyYoutubePj.dto.request.VideoCreationRequest;
import com.example.MyYoutubePj.service.VideoService;
import com.example.MyYoutubePj.service.YoutubeApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class YoutubeMetadataScheduler {

    private final YoutubeApiService youtubeApiService;
    private final VideoService videoService;

    private static final List<String> CATEGORY_IDS = List.of("1", "10", "20", "23", "24"); // Film, Music, Gaming, Comedy, Entertainment
    private static final int MAX_RESULTS = 50;
    private static final int DAILY_QUOTA_LIMIT = 10500;

    private int quotaCount = 0;
    private LocalDate lastQuotaResetDate = LocalDate.now();

    /**
     * Đồng bộ metadata YouTube mỗi phút
     */
    @Scheduled(fixedDelay = 60 * 1000) // Mỗi phút chạy 1 lần
    public void syncYoutubeMetadata() {
        resetQuotaIfNewDay();

        if (quotaCount >= DAILY_QUOTA_LIMIT) {
            log.warn("Quota limit reached: {} / {}. Skipping this run.", quotaCount, DAILY_QUOTA_LIMIT);
            return;
        }

        log.info("Starting YouTube sync. Current quota: {}", quotaCount);

        for (String categoryId : CATEGORY_IDS) {
            if (quotaCount >= DAILY_QUOTA_LIMIT) {
                log.warn("Stopping sync to avoid exceeding quota. Current quota: {}", quotaCount);
                break;
            }

            try {
                List<String> videoIds = youtubeApiService.getVideoIdsByTopic(categoryId, MAX_RESULTS);
                quotaCount += 100; // 100 quota cho search API

                List<VideoCreationRequest> videos = youtubeApiService.getVideosDetails(videoIds);
                quotaCount += 150; // 150 quota cho videos API

                videos.forEach(video -> {
                    log.debug("Saving video: {}", video.getTitle());
                    videoService.createVideoFromYouTubeApi(video);
                });


                log.info("Category {} synced. Current quota: {}", categoryId, quotaCount);
            } catch (Exception e) {
                log.error("Error syncing category {}: {}", categoryId, e.getMessage(), e);
            }
        }
    }

    /**
     * Reset quota mỗi ngày khi phát hiện ngày mới
     */
    private void resetQuotaIfNewDay() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastQuotaResetDate)) {
            quotaCount = 0;
            lastQuotaResetDate = today;
            log.info("New day detected. Quota count reset to 0.");
        }
    }
}

