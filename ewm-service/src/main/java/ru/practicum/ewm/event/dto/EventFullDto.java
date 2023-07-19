package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventStatus;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    long id;
    @NotBlank(message = "краткое описание события не пустое")
    String annotation;
    @NotNull(message = "категория не пустая")
    CategoryDto category;
    long confirmedRequests;
    LocalDateTime createdOn;
    String description;
    @NotNull(message = "дата и время события не пустые")
    LocalDateTime eventDate;
    @NotNull(message = "инициатор события не пустой")
    UserShortDto initiator;
    @NotNull(message = "место события не пустое")
    Location location;
    boolean paid;
    int participantLimit;
    LocalDateTime publishedOn;
    boolean requestModeration;
    EventStatus state;
    @NotBlank(message = "название события не пустое")
    String title;
    long views;
}
