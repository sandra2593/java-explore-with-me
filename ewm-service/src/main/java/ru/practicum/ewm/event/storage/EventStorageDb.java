package ru.practicum.ewm.event.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventStatus;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

public interface EventStorageDb extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findAllByCategoryId(long catId);

    List<Event> findAllByInitiator(User initiator, Pageable pageable);

    Optional<Event> findEventByIdAndState(long eventId, EventStatus state);

    Optional<Event> findEventByInitiatorIdAndId(long initiatorId, long eventId);
}
