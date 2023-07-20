package ru.practicum.ewm.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comment.model.CommentStatus;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentFullDto {
    long id;
    String text;
    UserDto author;
    EventFullDto event;
    CommentStatus state;
    LocalDateTime updatedOn;
    LocalDateTime createdOn;
}
