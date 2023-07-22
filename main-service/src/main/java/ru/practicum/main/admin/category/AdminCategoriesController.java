package ru.practicum.main.admin.category;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoriesController {
    private final AdminCategoriesService adminCategoriesService;

    public AdminCategoriesController(AdminCategoriesService adminCategoriesService) {
        this.adminCategoriesService = adminCategoriesService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addNewCategory(@Validated @RequestBody NewCategoryDto categoryDto) {
        return adminCategoriesService.addNewCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        adminCategoriesService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Validated @RequestBody CategoryDto categoryDto,
                                      @PathVariable Long catId) {
        return adminCategoriesService.updateCategory(catId, categoryDto);
    }
}