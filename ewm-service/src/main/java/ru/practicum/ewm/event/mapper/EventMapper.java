package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.mapper.UserMapper;

public class EventMapper {
    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(
                        CategoryDto.builder()
                                .id(event.getCategory().getId())
                                .name(event.getCategory().getName())
                                .build()
                )
                .eventDate(event.getEventDate())
                .initiator(
                        UserShortDto.builder()
                                .id(event.getInitiator().getId())
                                .name(event.getInitiator().getName())
                                .build()
                )
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(
                        CategoryDto.builder()
                                .id(event.getCategory().getId())
                                .name(event.getCategory().getName())
                                .build()
                )
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(
                        UserShortDto.builder()
                                .id(event.getInitiator().getId())
                                .name(event.getInitiator().getName())
                                .build()
                )
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static NewEventDto toNewEventDto(Event event) {
        return NewEventDto.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory().getId())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .build();
    }

    public static Event fromEventShortDto(EventShortDto eventShortDto) {
        Event event = new Event();
        event.setId(eventShortDto.getId());
        event.setAnnotation(eventShortDto.getAnnotation());
        event.setCategory(CategoryMapper.fromCategoryDto(eventShortDto.getCategory()));
        event.setEventDate(eventShortDto.getEventDate());
        event.setInitiator(UserMapper.fromUserShortDto(eventShortDto.getInitiator()));
        event.setPaid(eventShortDto.isPaid());
        event.setTitle(eventShortDto.getTitle());
        return event;
    }

    public static Event fromEventFullDto(EventFullDto eventFullDto) {
        Event event = new Event();
        event.setId(eventFullDto.getId());
        event.setAnnotation(eventFullDto.getAnnotation());
        event.setCategory(CategoryMapper.fromCategoryDto(eventFullDto.getCategory()));
        event.setCreatedOn(eventFullDto.getCreatedOn());
        event.setDescription(eventFullDto.getDescription());
        event.setEventDate(eventFullDto.getEventDate());
        event.setInitiator(UserMapper.fromUserShortDto(eventFullDto.getInitiator()));
        event.setLocation(eventFullDto.getLocation());
        event.setPaid(eventFullDto.isPaid());
        event.setParticipantLimit(eventFullDto.getParticipantLimit());
        event.setPublishedOn(eventFullDto.getPublishedOn());
        event.setRequestModeration(eventFullDto.isRequestModeration());
        event.setState(eventFullDto.getState());
        event.setTitle(eventFullDto.getTitle());
        return event;
    }

    public static Event fromNewEventDto(NewEventDto newEventDto, Category category) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setTitle(newEventDto.getTitle());
        return event;
    }
}
