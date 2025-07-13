package com.example.MyYoutubePj.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Channel {
    @Id
    @Column(length = 100)
    String channelId; // channelId chính thức từ YouTube

    String channelName;
    String channelImage; // link logo/thumbnail channel
    Long channelSubscribers;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Video> videos;
}
