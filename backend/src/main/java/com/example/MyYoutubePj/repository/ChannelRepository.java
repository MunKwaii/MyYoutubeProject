package com.example.MyYoutubePj.repository;

import com.example.MyYoutubePj.entity.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {
    @Query("SELECT c FROM Channel c WHERE c.channelImage IS NULL OR c.channelSubscribers IS NULL")
    Page<Channel> findOneMissingChannelInfo(Pageable pageable);
}

