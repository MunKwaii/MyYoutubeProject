package com.example.MyYoutubePj.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Quota {

    @Id
    private Long id = 1L;  // chỉ dùng 1 row duy nhất, id cố định

    private int currentQuota;
    private LocalDate lastUpdatedDate;
}

