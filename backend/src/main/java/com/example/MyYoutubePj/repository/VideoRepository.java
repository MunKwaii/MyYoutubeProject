package com.example.MyYoutubePj.repository;

import com.example.MyYoutubePj.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video,String> {

}
