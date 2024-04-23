package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.conflict.AlreadyExistException;
import ru.practicum.ewm.exception.conflict.ConflictOperationException;

import static ru.practicum.ewm.exception.NotFoundException.CATEGORY_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.CATEGORY_NOT_FOUND_MESSAGE;
import static ru.practicum.ewm.exception.conflict.AlreadyExistException.DUPLICATE_CATEGORY_NAME_ADVICE;
import static ru.practicum.ewm.exception.conflict.AlreadyExistException.DUPLICATE_CATEGORY_NAME_MESSAGE;
import static ru.practicum.ewm.exception.conflict.ConflictOperationException.CONFLICT_CATEGORY_DELETE_ADVICE;
import static ru.practicum.ewm.exception.conflict.ConflictOperationException.CONFLICT_CATEGORY_DELETE_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminCategoryService implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    /*--------------------Основные методы--------------------*/
    public Category saveCategory(CategoryDto categoryDto) {
        log.debug("Попытка сохранить новый объект Category.");
        Category category = CategoryMapper.mapToCategory(categoryDto);

        return saveCategoryObject(category);
    }

    public Category updateCategory(CategoryDto categoryDto, Long catId) {
        log.debug("Попытка обновить данные объекта Category.");
        Category category = updateCategoryObject(categoryDto, catId);

        return saveCategoryObject(category);
    }

    public String deleteCategory(Long catId) {
        log.debug("Попытка удалить объект Category по его Id.");
        // проверяем существует ли такая категория:
        getCategoryById(catId);
        // проверим есть ли связанные с этой категорией события:
        checkEventInCategory(catId);
        categoryRepository.deleteById(catId);

        return String.format("Категория с id = %d, удалена.", catId);
    }

    /*----------------Вспомогательные методы----------------*/
    private Category saveCategoryObject(Category category) {
        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.debug("{}: {}{}.", AlreadyExistException.class.getSimpleName(),
                    DUPLICATE_CATEGORY_NAME_MESSAGE, category.getName());
            throw new AlreadyExistException(DUPLICATE_CATEGORY_NAME_MESSAGE + category.getName(),
                    DUPLICATE_CATEGORY_NAME_ADVICE);
        }
    }

    private Category updateCategoryObject(CategoryDto categoryDto, Long catId) {
        // получаем объект из БД:
        Category category = getCategoryById(catId);
        // обновляем данные, если они инициализированы:
        category.setName(categoryDto.getName() != null ? categoryDto.getName() : category.getName());

        return category;
    }

    @Override
    public Category getCategoryById(Long catId) {
        log.debug("Попытка получить объект Category по его id.");
        return categoryRepository.findById(catId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), CATEGORY_NOT_FOUND_MESSAGE, catId);
            return new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE + catId, CATEGORY_NOT_FOUND_ADVICE);
        });
    }

    private void checkEventInCategory(Long catId) {
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            log.debug("{}: {}{}.", ConflictOperationException.class.getSimpleName(),
                    CONFLICT_CATEGORY_DELETE_MESSAGE, catId);
            throw new ConflictOperationException(CONFLICT_CATEGORY_DELETE_MESSAGE + catId,
                    CONFLICT_CATEGORY_DELETE_ADVICE);
        }
    }
}