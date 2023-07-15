package ru.practicum.main.category.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.service.CategoriesService;
import ru.practicum.main.category.dto.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping
    public Collection<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        return categoriesService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        return CategoryMapper.toCategoryDto(categoriesService.getCategory(catId));
    }
}