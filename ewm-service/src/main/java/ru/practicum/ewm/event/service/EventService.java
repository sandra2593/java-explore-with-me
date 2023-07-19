package ru.practicum.ewm.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.filter.EventAdminFilter;
import ru.practicum.ewm.event.filter.EventFilter;
import ru.practicum.ewm.event.filter.EventFilterSpecifications;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventStatus;
import ru.practicum.ewm.event.model.EventStatusAction;
import ru.practicum.ewm.event.storage.EventStorageDb;
import ru.practicum.ewm.exception.EventDateException;
import ru.practicum.ewm.exception.EventStatusException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.stats.StatsService;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.stats.dto.HitStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService implements EventServiceIntf {
    private final EventStorageDb eventStorage;
    private final UserService userService;
    private final StatsService statsService;
    private final CategoryService categoryService;


    @Autowired
    public EventService(EventStorageDb eventStorage, UserService userService, StatsService statsService, @Lazy CategoryService categoryService) {
        this.eventStorage = eventStorage;
        this.userService = userService;
        this.statsService = statsService;
        this.categoryService = categoryService;
    }

    @Override
    public List<EventFullDto> getAll(EventAdminFilter params, Pageable pageable) {
        List<Event> events = eventStorage.findAll(EventFilterSpecifications.getEventsAdminFilterSpecification(params), pageable).getContent();
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event modifiredEvent = EventMapper.fromEventFullDto(getEventById(eventId));
        if (Objects.nonNull(updateEventAdminRequest.getStateAction())) {
            if (updateEventAdminRequest.getStateAction().equals(EventStatusAction.PUBLISH_EVENT) && !modifiredEvent.getState().equals(EventStatus.PENDING)) {
                throw new EventStatusException("событие можно публиковать только из состояния ожидания");
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStatusAction.REJECT_EVENT) && modifiredEvent.getState().equals(EventStatus.PUBLISHED)) {
                throw new EventStatusException("нельзя отклонить, так как событие опубликовано");
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStatusAction.PUBLISH_EVENT) && ChronoUnit.HOURS.between(LocalDateTime.now(), modifiredEvent.getEventDate()) < 1) {
                throw new EventDateException("время события не меньше чем за два часа от настоящего момента");
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStatusAction.PUBLISH_EVENT)) {
                modifiredEvent.setState(EventStatus.PUBLISHED);
                modifiredEvent.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStatusAction.REJECT_EVENT)) {
                modifiredEvent.setState(EventStatus.CANCELED);
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStatusAction.SEND_TO_REVIEW)) {
                modifiredEvent.setState(EventStatus.PENDING);
            }
        }
        return partiallyUpdateEvent(modifiredEvent, updateEventAdminRequest.getAnnotation(), updateEventAdminRequest.getCategory(),
                updateEventAdminRequest.getDescription(), updateEventAdminRequest.getEventDate(), updateEventAdminRequest.getLocation(),
                updateEventAdminRequest.getPaid(), updateEventAdminRequest.getParticipantLimit(),
                updateEventAdminRequest.getRequestModeration(), updateEventAdminRequest.getTitle());

    }

    @Override
    public void update(Event updatedEvent) {
        eventStorage.save(updatedEvent);
    }

    @Override
    public List<EventShortDto> getAll(EventFilter params, Pageable pageable, HttpServletRequest request) {
        List<Event> events = eventStorage.findAll(EventFilterSpecifications.getEventsFilterSpecification(params), pageable).getContent();
        statsService.create(request.getRemoteAddr(), request.getRequestURI());
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublishedEventById(long eventId, HttpServletRequest request) {
        Optional<Event> optionalEvent = eventStorage.findEventByIdAndState(eventId, EventStatus.PUBLISHED);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            statsService.create(request.getRemoteAddr(), request.getRequestURI());
            List<HitStatsDto> stats = statsService.getStats(List.of(request.getRequestURI()), null, null, true);

            if (!event.getViews().equals(stats.get(0).getHits())) {
                event.setViews(stats.get(0).getHits());
                eventStorage.save(event);
            }
            return EventMapper.toEventFullDto(event);
        } else {
            throw new NotFoundException(String.format("нет события с id ", eventId));
        }
    }

    @Override
    public EventFullDto getEventById(long eventId) {
        Optional<Event> event = eventStorage.findById(eventId);
        if (event.isPresent()) {
            return EventMapper.toEventFullDto(event.get());
        } else {
            throw new NotFoundException(String.format("нет события с id ", eventId));
        }
    }

    @Override
    @Transactional
    public List<EventShortDto> getUserEvents(long userId, Pageable pageable) {
        List<Event> events = eventStorage.findAllByInitiator(UserMapper.fromUserDto(userService.getUserById(userId)), pageable);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addUserEvent(long userId, NewEventDto newEventDto) {
        if (ChronoUnit.HOURS.between(LocalDateTime.now(), newEventDto.getEventDate()) < 2) {
            throw new EventDateException("время события не меньше чем за два часа от настоящего момента");
        }
        if (newEventDto.getDescription().length() < 20 || newEventDto.getDescription().length() > 7000) {
            throw new EventDateException("поле description >= 20 && <= 7000, текущее: " + newEventDto.getDescription().length());
        }
        if (newEventDto.getAnnotation().length() < 20 || newEventDto.getAnnotation().length() > 2000) {
            throw new EventDateException("поле annotation >= 20 && <= 2000, текущее: " + newEventDto.getAnnotation().length());
        }
        if (newEventDto.getTitle().length() < 3 || newEventDto.getTitle().length() > 120) {
            throw new EventDateException("поле title >= 3 && <= 120, текущее: " + newEventDto.getTitle().length());
        }
        Category category = CategoryMapper.fromCategoryDto(categoryService.getCategoryById(newEventDto.getCategory()));
        Event newEvent = EventMapper.fromNewEventDto(newEventDto, category);
        newEvent.setInitiator(UserMapper.fromUserDto(userService.getUserById(userId)));

        return EventMapper.toEventFullDto(eventStorage.save(newEvent));
    }

    @Override
    public EventFullDto getUserEventById(long userId, long eventId) {
        Optional<Event> event = eventStorage.findEventByInitiatorIdAndId(userId, eventId);
        if (event.isPresent()) {
            return EventMapper.toEventFullDto(event.get());
        } else {
            throw new NotFoundException(String.format("нет события с id %s и инициатором с id ", eventId, userId));
        }
    }

    @Override
    public EventFullDto updateUserEventById(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event modifiredEvent = EventMapper.fromEventFullDto(getUserEventById(userId, eventId));

        if (modifiredEvent.getState().equals(EventStatus.PUBLISHED)) {
            throw new EventStatusException("нельзя изменить опубликованные события");
        }

        if (Objects.nonNull(updateEventUserRequest.getStateAction())) {
            if (updateEventUserRequest.getStateAction().equals(EventStatusAction.PUBLISH_EVENT)) {
                modifiredEvent.setState(EventStatus.PUBLISHED);
                modifiredEvent.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventUserRequest.getStateAction().equals(EventStatusAction.REJECT_EVENT) ||
                    updateEventUserRequest.getStateAction().equals(EventStatusAction.CANCEL_REVIEW)) {
                modifiredEvent.setState(EventStatus.CANCELED);
            }
            if (updateEventUserRequest.getStateAction().equals(EventStatusAction.SEND_TO_REVIEW)) {
                modifiredEvent.setState(EventStatus.PENDING);
            }
        }

        return partiallyUpdateEvent(modifiredEvent, updateEventUserRequest.getAnnotation(), updateEventUserRequest.getCategory(),
                updateEventUserRequest.getDescription(), updateEventUserRequest.getEventDate(), updateEventUserRequest.getLocation(),
                updateEventUserRequest.getPaid(), updateEventUserRequest.getParticipantLimit(),
                updateEventUserRequest.getRequestModeration(), updateEventUserRequest.getTitle());
    }

    @Override
    public List<EventFullDto> getEventsByIds(List<Long> eventIds) {
        return eventStorage.findAllById(eventIds).stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsByCategoryId(long catId) {
        return eventStorage.findAllByCategoryId(catId).stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    private EventFullDto partiallyUpdateEvent(Event modifiredEvent, String annotation, Long category,
            String description, LocalDateTime eventDate, Location location, Boolean paid,
            Integer participantLimit, Boolean requestModeration, String title) {

        if (Objects.nonNull(annotation)) {
            if (annotation.length() < 20 || annotation.length() > 2000) {
                throw new EventDateException("поле annotation >= 20 && <= 2000, текущее: " + annotation.length());
            }
            modifiredEvent.setAnnotation(annotation);
        }
        if (Objects.nonNull(category)) {
            modifiredEvent.setCategory(CategoryMapper.fromCategoryDto(categoryService.getCategoryById(category)));
        }
        if (Objects.nonNull(description)) {
            if (description.length() < 20 || description.length() > 7000) {
                throw new EventDateException("поле description >= 20 && <= 7000, текущее: " + description.length());
            }
            modifiredEvent.setDescription(description);
        }
        if (Objects.nonNull(location)) {
            modifiredEvent.setLocation(location);
        }
        if (Objects.nonNull(paid)) {
            modifiredEvent.setPaid(paid);
        }
        if (Objects.nonNull(participantLimit)) {
            modifiredEvent.setParticipantLimit(participantLimit);
        }
        if (Objects.nonNull(requestModeration)) {
            modifiredEvent.setRequestModeration(requestModeration);
        }
        if (Objects.nonNull(title)) {
            if (title.length() < 3 || title.length() > 120) {
                throw new EventDateException("поле title >= 3 && <= 120, текущее: " + title.length());
            }
            modifiredEvent.setTitle(title);
        }
        if (Objects.nonNull(eventDate)) {
            if (ChronoUnit.HOURS.between(LocalDateTime.now(), eventDate) < 2) {
                throw new EventDateException("время события не меньше чем за два часа от настоящего момента");
            }
            modifiredEvent.setEventDate(eventDate);
        }

        return EventMapper.toEventFullDto(eventStorage.save(modifiredEvent));
    }
}
