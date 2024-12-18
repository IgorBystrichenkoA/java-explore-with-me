package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.PageCategoryParam;

import java.util.List;

public interface CategoryService {

    Category create(CategoryDto category);

    Category update(CategoryDto updateCategoryDto);

    void delete(Long adminCategoryParam);

    List<Category> getAll(PageCategoryParam pageCategoryParam);

    Category getCategoryById(Long catId);
}
