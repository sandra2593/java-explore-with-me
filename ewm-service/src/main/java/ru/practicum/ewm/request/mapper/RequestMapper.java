package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.model.User;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder().id(request.getId()).created(request.getCreated()).event(request.getEvent().getId())
                .requester(request.getRequester().getId()).status(request.getStatus()).build();
    }

    public static Request fromRequestDto(RequestDto requestDto, Event event, User requester) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setCreated(requestDto.getCreated());
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(requestDto.getStatus());

        return request;
    }
}
