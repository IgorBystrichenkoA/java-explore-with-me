package ru.practicum.ewm.category.controller;

import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.PageCategoryParam;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(PageCategoryParam param) {
        return categoryService.getAll(param).stream().map(CategoryMapper::toCategoryDto).toList();
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return CategoryMapper.toCategoryDto(categoryService.getCategoryById(id));
    }
}
