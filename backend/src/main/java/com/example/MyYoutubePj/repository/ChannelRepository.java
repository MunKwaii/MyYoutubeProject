package com.example.MyYoutubePj.repository;

import com.example.MyYoutubePj.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {

}

