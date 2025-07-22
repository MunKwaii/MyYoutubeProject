package com.example.MyYoutubePj.repository;

import com.example.MyYoutubePj.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, String> {
    boolean existsByKeyword(String keyword);
}
