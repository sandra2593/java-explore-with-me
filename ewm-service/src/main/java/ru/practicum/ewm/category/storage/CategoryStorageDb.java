package ru.practicum.ewm.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.category.model.Category;

import java.util.Optional;

public interface CategoryStorageDb extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByName(String name);
}
