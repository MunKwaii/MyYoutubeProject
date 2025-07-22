package com.example.MyYoutubePj.service;

import com.example.MyYoutubePj.entity.Quota;
import com.example.MyYoutubePj.repository.QuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QuotaService {
    private final QuotaRepository quotaRepository;
    public Quota getOrInitializeQuota(){
        Quota quota = quotaRepository.findById(1L).orElseGet(Quota::new);
        LocalDate today = LocalDate.now();
        if (quota.getLastUpdatedDate() == null || !quota.getLastUpdatedDate().isEqual(today)){
            quota.setCurrentQuota(0);
            quota.setLastUpdatedDate(today);
            quotaRepository.save(quota);
        }
        return quota;
    }
    public void updateQuota(int newQuota){
        Quota quota = quotaRepository.findById(1L).orElseGet(Quota::new);
        quota.setCurrentQuota(newQuota);
        quotaRepository.save(quota);
    }
}
