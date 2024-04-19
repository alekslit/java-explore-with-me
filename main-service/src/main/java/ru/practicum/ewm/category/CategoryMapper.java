package ru.practicum.ewm.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {
    public static Category mapToCategory(CategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();

        return category;
    }

    public static CategoryDto mapToCategoryDto(Category category) {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        return categoryDto;
    }

    public static List<CategoryDto> mapToCategoryDto(List<Category> categories) {
        List<CategoryDto> categoryDtoList = categories.stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());

        return categoryDtoList;
    }
}