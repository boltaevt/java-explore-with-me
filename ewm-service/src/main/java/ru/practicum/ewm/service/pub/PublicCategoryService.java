package ru.practicum.ewm.service.pub;

import ru.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long catId);
}
