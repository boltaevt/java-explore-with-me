package ru.practicum.main.admin.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.repository.CategoriesRepository;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.error.EditingErrorException;
import ru.practicum.main.event.repository.EventsRepository;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AdminCategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventsRepository eventsRepository;

    public AdminCategoriesService(CategoriesRepository categoriesRepository, EventsRepository eventsRepository) {
        this.categoriesRepository = categoriesRepository;
        this.eventsRepository = eventsRepository;
    }

    public CategoryDto addNewCategory(NewCategoryDto categoryDto) {
        Category category = categoriesRepository.save(CategoryMapper.toCategory(categoryDto));
        log.info("Добавлена категория с id {}", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    public void deleteCategory(Long catId) {
        if (!eventsRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new EditingErrorException("Существуют связанные с категорией события.");
        }

        categoriesRepository.deleteById(catId);
        log.info("Удалена категория с id {}", catId);
    }

    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена или не существует"));

        category.setName(categoryDto.getName());
        Category updatedCategory = categoriesRepository.save(category);
        log.info("Обновлена категория с id {}", catId);

        return CategoryMapper.toCategoryDto(updatedCategory);
    }
}
