package ru.practicum.main.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoriesRepository;
import ru.practicum.main.error.EntityNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;

    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public Collection<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Collection<CategoryDto> categoryDtos = categoriesRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        log.info("Запрошено {} категорий.", categoryDtos.size());
        return categoryDtos;
    }

    public Category getCategory(Long catId) {
        Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория " + catId + " не найдена"));
        log.info("Запрошена категория {}", catId);

        return category;
    }
}