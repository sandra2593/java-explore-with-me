package ru.practicum.ewm.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryServiceIntf {
    CategoryDto add(NewCategoryDto newCategoryDto);

    void delete(long catId);

    CategoryDto update(long catId, CategoryDto categoryDto);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto getCategoryById(long catId);
}
