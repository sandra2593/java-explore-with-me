package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank(message = "краткое описание события не пустое")
    String annotation;
    long category;
    @NotBlank(message = "описание события не пустое")
    String description;
    @NotNull(message = "дата и время события не пустые")
    LocalDateTime eventDate;
    @NotNull(message = "место события не пустое")
    Location location;
    boolean paid = false;
    int participantLimit = 0;
    boolean requestModeration = true;
    @NotBlank(message = "название события не пустое")
    private String title;
}
