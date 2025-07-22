package com.example.MyYoutubePj.service;

import com.example.MyYoutubePj.entity.Keyword;
import com.example.MyYoutubePj.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;

//    public void saveKeywordBuster(List<String> keywords){
//        List<Keyword> keywordEntities = keywords.stream()
//                .map(k -> new Keyword(k, null, 0))
//                .toList();
//        keywordRepository.saveAll(keywordEntities);
//    }
    public void saveKeywordBuster(List<String> keywords){
        keywords.forEach(k -> {
            if (!keywordRepository.existsByKeyword(k)) {  // kiểm tra trước khi lưu
                Keyword keyword = new Keyword(k, null, 0);
                keywordRepository.save(keyword);
            }
        });
    }
    public Optional<Keyword> getCurrentOrNextKeyword() {
        return keywordRepository.findAll().stream()
                .filter(k -> k.getNextPageToken() != null || k.getVideoCount() == 0)
                .findFirst();
    }
    public void updateKeyword(Keyword keyword, String nextPageToken, long videoCount) {
        keyword.setNextPageToken(nextPageToken);
        keyword.setVideoCount(videoCount);
        keywordRepository.save(keyword);
    }
}
