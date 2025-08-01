package com.example.MyYoutubePj.controller;

import com.example.MyYoutubePj.dto.request.VideoCreationRequest;
import com.example.MyYoutubePj.dto.response.VideoMainPageResponse;
import com.example.MyYoutubePj.dto.response.VideoPlayPageResponse;
import com.example.MyYoutubePj.entity.Video;
import com.example.MyYoutubePj.service.VideoService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://127.0.0.1:5500")  // hoặc "http://localhost:5500"
@RestController
@RequestMapping("/api/videos")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoController {
    final VideoService videoService;
    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }
    /**
     * Lấy toàn bộ video từ DB (metadata đầy đủ)
     * Endpoint: GET /api/videos
     */
    @GetMapping
    public List<Video> getAllVideos() {
        return videoService.getAllVideo();
    }

    /**
     * Lấy video theo ID
     * Endpoint: GET /api/videos/{id}
     */
//    public Video getVideoById(@PathVariable String id) {
//        return videoService.getVideoById(id)
//                .orElseThrow(() -> new RuntimeException("Video with id " + id + " not found"));
//    }
    @GetMapping("/{id}")
    public ResponseEntity<VideoPlayPageResponse> getVideoById(@PathVariable String id) {
        return videoService.getVideoDetailById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video with id " + id + " not found"));
    }
    /**
     * Lấy random seed
     * Endpoint: GET /api/videos/random-seed
     */
    @GetMapping("/random-seed")
    public Map<String, Object> generateRandomSeed() {
        String seed = UUID.randomUUID().toString();
        long totalVideos = videoService.countVideos();
        return Map.of(
                "seed", seed,
                "totalVideos", totalVideos
        );
    }
    /**
     * Lấy danh sách video từ seed cố định
     * Endpoint: GET /api/videos/by-seed
     */
    @GetMapping("/by-seed")
    public List<VideoMainPageResponse> getVideosBySeed(
            @RequestParam String seed,
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        return videoService.getVideosBySeed(seed, page, pageSize);
    }
    /**
     * Lấy danh sách video từ keyword search
     * Endpoint: GET /api/videos/search
     */
    @GetMapping("/search")
    public List<VideoMainPageResponse> searchVideos(
            @RequestParam String keyword,
            @RequestParam String seed,
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        return videoService.searchVideos(keyword, seed, page, pageSize);
    }



//    //creat new video
//    @PostMapping
//    public Video createVideo(@RequestBody VideoCreationRequest request) {
//        return videoService.createVideo(request);
//    }
//    //update video
//    @PutMapping("/{id}")
//    public Video updateVideo(@PathVariable String id, @RequestBody Video video) {
//        return videoService.updateVideo(id, video);
//    }
//    //delete video
//    @DeleteMapping("/{id}")
//    public void deleteVideoBy(@PathVariable String id) {
//        videoService.deleteVideo(id);
//    }
}
