package com.example.MyYoutubePj.repository;

import com.example.MyYoutubePj.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
