package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;

import java.util.List;

public interface RequestServiceIntf {
    List<RequestDto> getAll(long userId);

    RequestDto getRequestById(long requestId);

    RequestDto add(long userId, long eventId);

    RequestDto cancel(long userId, long requestId);

    List<RequestDto> getUserEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateUserEventRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
    );
}
