package ru.practicum.ewm.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.request.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {
    long id;
    LocalDateTime created;
    long event;
    long requester;
    RequestStatus status;
}
