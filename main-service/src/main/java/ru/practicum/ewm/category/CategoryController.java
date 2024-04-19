package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.service.AdminCategoryService;
import ru.practicum.ewm.category.service.PublicCategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final AdminCategoryService adminService;
    private final PublicCategoryService publicService;

    /*--------------------Основные Admin методы--------------------*/
    @PostMapping("admin/categories")
    public CategoryDto saveCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return CategoryMapper.mapToCategoryDto(adminService.saveCategory(categoryDto));
    }

    @PatchMapping("admin/categories/{catId}")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      @PathVariable Long catId) {
        return CategoryMapper.mapToCategoryDto(adminService.updateCategory(categoryDto, catId));
    }

    @DeleteMapping("admin/categories/{catId}")
    public String deleteCategory(@PathVariable Long catId) {
        return adminService.deleteCategory(catId);
    }

    /*--------------------Основные Public методы--------------------*/
    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return CategoryMapper.mapToCategoryDto(publicService.getCategories(from, size));
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        return CategoryMapper.mapToCategoryDto(publicService.getCategoryById(catId));
    }
}