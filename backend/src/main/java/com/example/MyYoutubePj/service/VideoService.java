package com.example.MyYoutubePj.service;

import com.example.MyYoutubePj.dto.request.ChannelRequest;
import com.example.MyYoutubePj.dto.request.VideoCreationRequest;
import com.example.MyYoutubePj.entity.Channel;
import com.example.MyYoutubePj.entity.Video;
import com.example.MyYoutubePj.repository.ChannelRepository;
import com.example.MyYoutubePj.repository.VideoRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class VideoService {
    final VideoRepository videoRepository;
    final ChannelRepository channelRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, ChannelRepository channelRepository) {
        this.videoRepository = videoRepository;
        this.channelRepository = channelRepository;
    }

    public Video createVideoFromYouTubeApi(VideoCreationRequest request) {
        // Kiểm tra hoặc tạo mới Channel
        Channel channel = channelRepository.findById(request.getChannel().getChannelId())
                .orElseGet(() -> {
                    Channel newChannel = Channel.builder()
                            .channelId(request.getChannel().getChannelId())
                            .channelName(request.getChannel().getChannelName())
                            .build();
                    return channelRepository.save(newChannel);
                });

        String videoUrl = "https://www.youtube.com/watch?v=" + request.getVideoId();

        Video video = Video.builder()
                .videoUrl(videoUrl)
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .views(request.getViews() != null ? request.getViews() : 0L)
                .likes(request.getLikes() != null ? request.getLikes() : 0L)
                .comments(request.getComments() != null ? request.getComments() : 0L)
                .duration(request.getDuration())
                .tags(request.getTags())
                .publishedAt(request.getPublishedAt())
                .channel(channel)
                .build();

        return videoRepository.save(video);
    }

    public Channel updateChannelInfoFromYouTubeApi(String channelId, ChannelRequest request) {
        return channelRepository.findById(channelId)
                .map(channel -> {
                    channel.setChannelImage(request.getChannelImage());
                    channel.setChannelSubscribers(request.getChannelSubscribers());
                    return channelRepository.save(channel);
                })
                .orElseThrow(() -> new RuntimeException("Channel not found"));
    }

    public List<Video> getAllVideo() {
        return videoRepository.findAll();
    }

    public Optional<Video> getVideoById(String id) {
        return videoRepository.findById(id);
    }

    public void deleteVideo(String id) {
        videoRepository.deleteById(id);
    }
}
