package com.example.MyYoutubePj.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelRequest {
    String channelId;
    String channelName;
    String channelImage; //Tam thoi null, goi API kenh sau
    Long channelSubscribers; //Tam thoi null, goi API kenh sau
}
