package com.example.MyYoutubePj.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "category")
@Data
public class Category {
    @Id
    private String categoryId; // Primary key là category
    private String nextPageToken;
    private long videoCount; // Số video đã lấy cho category này
}
