package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.service.AdminCategoryService;
import ru.practicum.ewm.category.service.PublicCategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final AdminCategoryService adminService;
    private final PublicCategoryService publicService;

    /*--------------------Основные Admin методы--------------------*/
    @PostMapping("admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return CategoryMapper.mapToCategoryDto(adminService.saveCategory(categoryDto));
    }

    @PatchMapping("admin/categories/{catId}")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      @PathVariable Long catId) {
        return CategoryMapper.mapToCategoryDto(adminService.updateCategory(categoryDto, catId));
    }

    @DeleteMapping("admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteCategory(@PathVariable Long catId) {
        return adminService.deleteCategory(catId);
    }

    /*--------------------Основные Public методы--------------------*/
    @GetMapping("/categories")
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр запроса from, должен быть " +
                    "положительным числом или нулём.") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "Параметр запроса size, должен быть " +
                    "положительным числом.") Integer size) {
        return CategoryMapper.mapToCategoryDto(publicService.getCategories(from, size));
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        return CategoryMapper.mapToCategoryDto(publicService.getCategoryById(catId));
    }
}