package ru.practicum.ewm.category.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.PageCategoryParam;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.data.OffsetBasedPageRequest;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConditionsNotRespected;
import ru.practicum.ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    private static final String CATEGORY_ALREADY_EXIST = "Категория %s уже существует";
    private static final String NOT_EXISTING_CATEGORY = "Категория по id %s, не существует";
    private static final String CATEGORY_IS_INDICATED_IN_EVENTS = "Категория по id %s, используется в событиях";

    @Transactional
    public Category create(CategoryDto categoryDto) {
        log.info("ADMIN: Получен запрос на создание категории {}", categoryDto);

        categoryRepository.findByName(categoryDto.getName())
                .ifPresent(category -> {
                    throw new ConditionsNotRespected(String.format(CATEGORY_ALREADY_EXIST, category.getName()));
                });

        Category categorySaved = categoryRepository.save(new Category(categoryDto.getName()));

        log.info("ADMIN: Создана новая категория {}", categorySaved);
        return categorySaved;
    }

    @Transactional
    public void delete(Long categoryId) {
        log.info("ADMIN: Получен запрос на удаление категории по id {}", categoryId);

        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_EXISTING_CATEGORY, categoryId)));
        BooleanExpression categoryIdQ = QEvent.event.category.id.eq(categoryId);
        if (eventRepository.exists(categoryIdQ)) {
            throw new ConditionsNotRespected(String.format(CATEGORY_IS_INDICATED_IN_EVENTS, categoryId));
        }

        categoryRepository.deleteById(categoryId);
        log.info("ADMIN: Категория по id {} успешно удалена", categoryId);
    }

    @Transactional
    public Category update(CategoryDto updateCategoryDto) {
        log.info("ADMIN: Получен запрос на изменение категории по id {} на категорию {}", updateCategoryDto.getId(),
                updateCategoryDto.getName());

        String name = updateCategoryDto.getName();
        Long id = updateCategoryDto.getId();

        categoryRepository.findByName(name).ifPresent(existingCategory -> {
            if (!existingCategory.getId().equals(id)) {
                throw new ConditionsNotRespected(String.format(CATEGORY_ALREADY_EXIST, name));
            }
        });

        Category categoryFromDb = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_EXISTING_CATEGORY, id)));
        categoryFromDb.setName(name);
        Category newCategory = categoryRepository.save(categoryFromDb);

        log.info("ADMIN: Категория {} успешно изменена", newCategory);
        return newCategory;
    }

    @Override
    public List<Category> getAll(PageCategoryParam param) {
        log.info("PUBLIC: Запрос на получение всех категорий");

        Pageable pageable = new OffsetBasedPageRequest(param.getFrom(), param.getSize());
        List<Category> categoriesFromDb = categoryRepository.findAll(pageable).getContent();

        log.info("PUBLIC: Получен список всех категорий с id от {} размером {}", param.getFrom(), param.getSize());
        return categoriesFromDb;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        log.info("PUBLIC: Получен запрос по получению категории по id {}", categoryId);

        Category categoryFromDb = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_EXISTING_CATEGORY, categoryId)));

        log.info("PUBLIC: Получена категория {} по id {}", categoryFromDb.getName(), categoryId);
        return categoryFromDb;
    }
}
