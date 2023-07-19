package ru.practicum.ewm.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventStatus;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.CantChangeStatusException;
import ru.practicum.ewm.exception.CantParticipateInEventException;
import ru.practicum.ewm.exception.DuplicateException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.storage.RequestStorageDb;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestService implements RequestServiceIntf {
    private final RequestStorageDb requestStorage;
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public RequestService(RequestStorageDb requestStorage, UserService userService, EventService eventService) {
        this.requestStorage = requestStorage;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public List<RequestDto> getAll(long userId) {
        User requester = UserMapper.fromUserDto(userService.getUserById(userId));
        return requestStorage.findAllByRequester(requester).stream()
                .map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public RequestDto getRequestById(long requestId) {
        Optional<Request> request = requestStorage.findById(requestId);
        if (Objects.nonNull(request)) {
            return RequestMapper.toRequestDto(request.get());
        } else {
            throw new NotFoundException(String.format("нет запроса с id ", requestId));
        }
    }

    @Override
    @Transactional
    public RequestDto add(long userId, long eventId) {
        if (requestStorage.findUserRequestToEvent(eventId, userId).isPresent()) {
            throw new DuplicateException("повторный запрос на участие в событии");
        }
        User requester = UserMapper.fromUserDto(userService.getUserById(userId));
        Event event = EventMapper.fromEventFullDto(eventService.getEventById(eventId));

        long eventRequestsNum = requestStorage.findCountOfConfirmedRequests(event, RequestStatus.CONFIRMED);

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new CantParticipateInEventException("нельзя участвовать в несуществующем событии");
        }
        if (event.getInitiator().equals(requester)) {
            throw new CantParticipateInEventException("запрос на участие в своём событии");
        }
        if (eventRequestsNum >= event.getParticipantLimit() && event.getParticipantLimit() > 0) {
            event.setAvailable(false);
            eventService.update(event);
            throw new CantParticipateInEventException("слишком много запросов на участие");
        }

        Request request = new Request();
        request.setRequester(requester);
        request.setEvent(event);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            long confirmedRequests = event.getConfirmedRequests();
            event.setConfirmedRequests(++confirmedRequests);
            eventService.update(event);
        }

        try {
            return RequestMapper.toRequestDto(requestStorage.save(request));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateException(String.format("уже существует ваш запрос на участие в событии с id ", eventId));
        }
    }


    @Override
    @Transactional
    public RequestDto cancel(long userId, long requestId) {
        User requester = UserMapper.fromUserDto(userService.getUserById(userId));
        RequestDto requestDto = getRequestById(requestId);

        if (requestDto.getRequester() != userId) {
            throw new CantChangeStatusException("отменить запрос может только его автор");
        }

        Event event = EventMapper.fromEventFullDto(eventService.getEventById(requestDto.getEvent()));
        Request request = RequestMapper.fromRequestDto(getRequestById(requestId), event, requester);
        request.setStatus(RequestStatus.CANCELED);
        long confirmedRequests = event.getConfirmedRequests();
        event.setConfirmedRequests(--confirmedRequests);

        if (!event.isAvailable()) {
            event.setAvailable(true);
        }

        eventService.update(event);

        return RequestMapper.toRequestDto(requestStorage.save(request));
    }

    @Override
    public List<RequestDto> getUserEventRequests(long userId, long eventId) {
        return requestStorage.findAllUserEventRequests(userId, eventId).stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateUserEventRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        List<Request> requests = requestStorage.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        Event event = EventMapper.fromEventFullDto(eventService.getUserEventById(userId, eventId));
        User user = UserMapper.fromUserDto(userService.getUserById(userId));

        long eventRequestsNum = requestStorage.findCountOfConfirmedRequests(event, RequestStatus.CONFIRMED);

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return EventRequestStatusUpdateResult.builder().confirmedRequests(confirmedRequests).rejectedRequests(rejectedRequests).build();
        }

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new CantChangeStatusException("Статус меняется только для заявок в статусе ожидает");
            }

            if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED) &&
                    eventRequestsNum >= event.getParticipantLimit() && event.getParticipantLimit() > 0) {
                event.setAvailable(false);
                eventService.update(event);
                throw new CantParticipateInEventException("слишком много запросов на участие");
            }

            request.setStatus(eventRequestStatusUpdateRequest.getStatus());
            if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(RequestMapper.toRequestDto(request));
            } else {
                rejectedRequests.add(RequestMapper.toRequestDto(request));
            }
        }

        if (!confirmedRequests.isEmpty()) {
            requestStorage.saveAll(
                    confirmedRequests.stream().map(req -> RequestMapper.fromRequestDto(req, event, user)).collect(Collectors.toList())
            );
            long eventConfirmedRequests = event.getConfirmedRequests();
            event.setConfirmedRequests(eventConfirmedRequests + confirmedRequests.size());
            eventService.update(event);
        }

        if (!rejectedRequests.isEmpty()) {
            requestStorage.saveAll(
                    rejectedRequests.stream().map(req -> RequestMapper.fromRequestDto(req, event, user)).collect(Collectors.toList())
            );

            long eventConfirmedRequests = event.getConfirmedRequests();
            event.setConfirmedRequests(eventConfirmedRequests - confirmedRequests.size());
            eventService.update(event);
        }

        return EventRequestStatusUpdateResult.builder().confirmedRequests(confirmedRequests).rejectedRequests(rejectedRequests).build();
    }
}
