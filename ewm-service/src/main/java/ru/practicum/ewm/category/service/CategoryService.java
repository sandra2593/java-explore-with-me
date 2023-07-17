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
import ru.practicum.ewm.exception.EventDateException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
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
        if (newCategoryDto.getName().length() > 50) {
            throw new EventDateException("поле name <= 50, текущее: " + newCategoryDto.getName().length());
        }

        Category catWithName = categoryStorage.findCategoryByName(newCategoryDto.getName()).orElse(null);
        if (catWithName != null) {
            throw new DuplicateException(String.format("есть такая категория ", catWithName));
        }

        Category category = CategoryMapper.fromNewCategoryDto(newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryStorage.save(category));
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
        if (categoryDto.getName().length() > 50) {
            throw new EventDateException("поле name <= 50, текущее: " + categoryDto.getName().length());
        }

        Category catWithName = categoryStorage.findCategoryByName(categoryDto.getName()).orElse(null);
        Category categoryToUpdate = CategoryMapper.fromCategoryDto(getCategoryById(catId));
        if ((catWithName != null) && (catWithName.getId() != catId)) {
            throw new DuplicateException(String.format("есть такая категория ", catWithName));
        }

        categoryToUpdate.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryStorage.save(categoryToUpdate));
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryStorage.findAll(pageable).stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        Optional<Category> category = categoryStorage.findById(catId);
        if (category.isPresent()) {
            return CategoryMapper.toCategoryDto(category.get());
        } else {
            throw new NotFoundException(String.format("нет категории с id ", catId));
        }
    }
}
