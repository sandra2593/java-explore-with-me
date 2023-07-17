package ru.practicum.ewm.user.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorageDb extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> userIds, Pageable pageable);

    Optional<User> findUserByName(String name);
}
