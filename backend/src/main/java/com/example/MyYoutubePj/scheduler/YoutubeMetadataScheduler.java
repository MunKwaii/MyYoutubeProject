package com.example.MyYoutubePj.scheduler;

import com.example.MyYoutubePj.dto.request.ChannelRequest;
import com.example.MyYoutubePj.dto.request.VideoCreationRequest;
import com.example.MyYoutubePj.dto.response.YoutubeVideoIdPageResponse;
import com.example.MyYoutubePj.entity.*;
import com.example.MyYoutubePj.service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class YoutubeMetadataScheduler {

    private final YoutubeApiService youtubeApiService;
    private final VideoService videoService;
    private final QuotaService quotaService;
    private final CategoryService categoryService;
    private final KeywordService keywordService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
//    private static final List<String> CATEGORY_IDS = List.of(
//            "1",  "2",  "10", "15", "17", "18", "19", "20", "21", "22",
//            "23", "24", "25", "26", "27", "28", "29", "30", "31", "32",
//            "33", "34", "35", "36", "37", "38", "39", "40", "41", "42",
//            "43", "44"
//    );
    // Tất cả category của youtube api (regioncode: us)
    private static final int MAX_RESULTS = 50;
    private static final int DAILY_QUOTA_LIMIT = 50000;
    private int quotaCount;
    @Setter
    private String keyword;
    @PostConstruct
    public void init(){
        Quota quota = quotaService.getOrInitializeQuota();
        this.quotaCount = quota.getCurrentQuota();
        log.info("Quota count khởi động; {}, last updated: {}", quotaCount, quota.getLastUpdatedDate());
    }
//    @Scheduled(fixedDelay = 1000) // Delay ngắn nhất có thể để tận dụng quota  => Thay lại bằng lớp start và stop để quản lý chạy ngầm
    public void startScheduler(){
        if (scheduledFuture == null || scheduledFuture.isCancelled()){ // Điều kiện đầu hiện tại chưa cần thiết, nhưng sẽ hữu dụng khi cho phép api frontend gọi hàm này thủ công nếu có
            scheduledFuture = scheduler.scheduleWithFixedDelay(this::syncYoutubeMetadata, 0, 10, TimeUnit.MICROSECONDS);
            log.info("Scheduler start");
        }
        else{
            log.info("Scheduler started, no need to start");
        }
    }
    public void stopScheduler(){
        if (scheduledFuture != null && !scheduledFuture.isCancelled()){
            scheduledFuture.cancel(false);
            log.info("Scheduler stop");
        }
    }
    public void parseAndSaveKeywords(List<String> keywords){
        keywordService.saveKeywordBuster(keywords);
        log.info("Saved {} keywords into database", keywords.size());
    }

    public void syncYoutubeMetadata() {
        if (quotaCount >= DAILY_QUOTA_LIMIT) {
            log.warn("Quota hết: {}/{}. Dừng scheduler.", quotaCount, DAILY_QUOTA_LIMIT);
            stopScheduler();
            return;
        }
        keywordService.getCurrentOrNextKeyword().ifPresentOrElse(
                keywordEntity -> {
                    log.info("Processing keyword: {}", keywordEntity.getKeyword());
                    processKeyword(keywordEntity);
                },
                () -> {
                    log.info("All keywords are killed");
                    stopScheduler();
                }
        );
    }
    public void syncOneMissingChannel() {
        if (quotaCount >= DAILY_QUOTA_LIMIT) {
            log.warn("Quota hết: {}/{}. Dừng scheduler.", quotaCount, DAILY_QUOTA_LIMIT);
            stopScheduler();
            return;
        }
        Optional<Channel> missingChannel = videoService.findOneChannelWithMissingInfo();

        if (missingChannel.isEmpty()) {
            log.info("✅ Không còn channel nào thiếu thông tin.");
            return;
        }
        Channel channel = missingChannel.get();
        String channelId = channel.getChannelId();

        try {
            ChannelRequest request = youtubeApiService.getChannelInfo(channelId);

            if (request != null) {
                videoService.updateChannelInfoFromYouTubeApi(request);
                log.info("✅ Đã cập nhật channelId: {}", channelId);
            } else {
                log.warn("⚠️ Không lấy được thông tin từ API cho channelId: {}", channelId);
            }
        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật channelId {}: {}", channelId, e.getMessage());
        }
    }
    private void processKeyword(Keyword keyword) {
        String nextPageToken = keyword.getNextPageToken();
        long totalSaved = keyword.getVideoCount();
        String keywordText = keyword.getKeyword();
        do{
            YoutubeVideoIdPageResponse videoIdPage = youtubeApiService.getVideoIdsByKeyword(MAX_RESULTS, keywordText, nextPageToken);
            quotaCount += 100;
            quotaService.updateQuota(quotaCount);
            List<String> newVideoIds = videoIdPage.getVideoIds().stream()
                    .filter(id -> !videoService.existsByVideoId(id))
                    .toList();
            if (!newVideoIds.isEmpty()) {
                List<VideoCreationRequest> videos = youtubeApiService.getVideosDetails(newVideoIds);
                quotaCount += 150;
                quotaService.updateQuota(quotaCount);
                for (VideoCreationRequest video : videos) {
                    Video savedVideo = videoService.createVideoFromYouTubeApi(video);
                    if (savedVideo != null) {
                        totalSaved ++;
                        log.info("Saved video: {} - {}", savedVideo.getTitle(), savedVideo.getVideoUrl());
                    }
                }
            }
            nextPageToken = videoIdPage.getNextPageToken();
            keywordService.updateKeyword(keyword,nextPageToken, totalSaved );
        } while (nextPageToken != null);
        log.info("Finished keyword {}, total saved videos: {}", keywordText, totalSaved);
    }
}

