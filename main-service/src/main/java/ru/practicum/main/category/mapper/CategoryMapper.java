package ru.practicum.main.category.mapper;

import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.model.Category;

public class CategoryMapper {
    private CategoryMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Category toCategory(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}