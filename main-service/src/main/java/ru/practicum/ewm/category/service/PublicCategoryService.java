package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

import static ru.practicum.ewm.exception.NotFoundException.CATEGORY_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.CATEGORY_NOT_FOUND_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicCategoryService implements CategoryService {
    private final CategoryRepository categoryRepository;

    /*--------------------Основные методы--------------------*/
    public List<Category> getCategories(Integer from, Integer size) {
        log.debug("Попытка получить список объектов Category.");
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Category> categoryList = categoryRepository.findAll(pageRequest).getContent();

        return categoryList;
    }

    @Override
    public Category getCategoryById(Long catId) {
        log.debug("Попытка получить объект Category по его id.");
        Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), CATEGORY_NOT_FOUND_MESSAGE, catId);
            return new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE + catId, CATEGORY_NOT_FOUND_ADVICE);
        });

        return category;
    }
}