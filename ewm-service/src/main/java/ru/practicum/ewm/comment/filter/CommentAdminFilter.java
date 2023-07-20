package ru.practicum.ewm.comment.filter;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comment.model.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentAdminFilter {
    List<Long> users;
    List<Long> events;
    List<CommentStatus> commentStates;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
}
