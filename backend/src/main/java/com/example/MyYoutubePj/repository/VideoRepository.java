package com.example.MyYoutubePj.repository;

import com.example.MyYoutubePj.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video,String> {
    boolean existsByVideoUrl(String videoUrl);
    @Query(value = "SELECT * FROM videos ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Video> findRandomVideos(@Param("limit") int limit);
    @Query(value = "SELECT id FROM videos", nativeQuery = true)
    List<String> findAllVideoIds();
    @Query(value = "SELECT v.id FROM videos v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    List<String> findVideoIdsByKeyword(@Param("keyword") String keyword);

}
