package com.example.MyYoutubePj.controller;

import com.example.MyYoutubePj.dto.request.VideoCreationRequest;
import com.example.MyYoutubePj.entity.Video;
import com.example.MyYoutubePj.service.VideoService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/{id}")
    public Video getVideoById(@PathVariable String id) {
        return videoService.getVideoById(id)
                .orElseThrow(() -> new RuntimeException("Video with id " + id + " not found"));
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
