package com.example.MyYoutubePj.service;

import com.example.MyYoutubePj.entity.Category;
import com.example.MyYoutubePj.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private static final String DEFAULT_CATEGORY_ID = "1"; // Hoặc category đầu tiên bạn muốn

    public Category getOrInitCategory() {
        return getOrInitCategory(DEFAULT_CATEGORY_ID);
    }

    // Method mới để lấy category theo id chỉ định
    public Category getOrInitCategory(String categoryId) {
        log.info("currentCategoryId: {}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseGet(() -> {
                    log.info("Không tìm thấy category, tạo mới.");
                    Category category = new Category();
                    category.setCategoryId(categoryId);
                    category.setNextPageToken(null);
                    category.setVideoCount(0);
                    return categoryRepository.save(category);
                });
    }

    public void updateCategory(Category category, String nextPageToken, long newVideoCount) {
        category.setNextPageToken(nextPageToken);
        category.setVideoCount(newVideoCount);
        categoryRepository.save(category);
    }

    public void switchToNextCategory(Category category, String newCategoryId) {
        Category newCategory = new Category();
        newCategory.setCategoryId(newCategoryId);
        newCategory.setNextPageToken(null);
        newCategory.setVideoCount(0);
        categoryRepository.save(newCategory);
    }
}

