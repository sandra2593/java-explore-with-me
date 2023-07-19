package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.filter.EventAdminFilter;
import ru.practicum.ewm.event.filter.EventFilter;
import ru.practicum.ewm.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventServiceIntf {
    List<EventFullDto> getAll(EventAdminFilter params, Pageable pageable);

    EventFullDto updateAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    void update(Event updatedEvent);

    List<EventShortDto> getAll(EventFilter params, Pageable pageable, HttpServletRequest request);

    EventFullDto getPublishedEventById(long eventId, HttpServletRequest request);

    EventFullDto getEventById(long eventId);

    List<EventShortDto> getUserEvents(long userId, Pageable pageable);

    EventFullDto addUserEvent(long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(long userId, long eventId);

    EventFullDto updateUserEventById(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getEventsByIds(List<Long> eventIds);

    List<EventFullDto> getEventsByCategoryId(long catId);
}
