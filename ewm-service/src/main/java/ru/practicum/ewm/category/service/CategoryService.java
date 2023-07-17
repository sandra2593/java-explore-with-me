package ru.practicum.ewm.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryStorageDb;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.CantDeleteCategoryWithEventsException;
import ru.practicum.ewm.exception.DuplicateException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService implements CategoryServiceIntf {
    private final CategoryStorageDb categoryStorage;
    private final EventService eventService;

    @Autowired
    public CategoryService(CategoryStorageDb categoryStorage, @Lazy EventService eventService) {
        this.categoryStorage = categoryStorage;
        this.eventService = eventService;
    }

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.fromNewCategoryDto(newCategoryDto);
        try {
            return CategoryMapper.toCategoryDto(categoryStorage.save(category));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateException(String.format("есть такая категория ", category.getName()));
        }
    }

    @Override
    @Transactional
    public void delete(long catId) {
        List<EventFullDto> eventsOfThisCategory = eventService.getEventsByCategoryId(getCategoryById(catId).getId());
        if (eventsOfThisCategory.isEmpty()) {
            categoryStorage.deleteById(getCategoryById(catId).getId());
        } else {
            throw new CantDeleteCategoryWithEventsException(String.format("нельзя удалить категорию с событиями ", catId));
        }
    }

    @Override
    public CategoryDto update(long catId, CategoryDto categoryDto) {
        Category categoryToUpdate = CategoryMapper.fromCategoryDto(getCategoryById(catId));
        categoryToUpdate.setName(categoryDto.getName());
        try {
            return CategoryMapper.toCategoryDto(categoryStorage.save(categoryToUpdate));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateException(String.format("есть такая категория ", categoryDto.getName()));
        }
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryStorage.findAll(pageable).stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        Optional<Category> category = categoryStorage.findById(catId);
        if (Objects.nonNull(category)) {
            return CategoryMapper.toCategoryDto(category.get());
        } else {
            throw new NotFoundException(String.format("нет категории с id ", catId));
        }
    }
}
