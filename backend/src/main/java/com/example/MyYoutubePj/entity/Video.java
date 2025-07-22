package com.example.MyYoutubePj.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String Id;
    @Column(name = "videoUrl", unique = true, nullable = false)
    String videoUrl; // URL embedded của video

    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    String thumbnail;

    Long views;
    Long likes;
    Long comments;

    String duration; // ISO 8601
    Timestamp publishedAt;

    @ElementCollection
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tags", columnDefinition = "TEXT")
    List<String> tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", referencedColumnName = "channelId")
    Channel channel; // Mối quan hệ tới Channel entity

    @Column(name = "created_at", updatable = false)
    Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
}
