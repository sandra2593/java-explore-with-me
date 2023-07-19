package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    long id;
    @NotBlank(message = "краткое описание события не пустое")
    String annotation;
    @NotNull(message = "категория не пустая")
    CategoryDto category;
    long confirmedRequests;
    @NotBlank(message = "дата и время события не пустые")
    LocalDateTime eventDate;
    @NotNull(message = "инициатор события не пустой")
    UserShortDto initiator;
    boolean paid;
    @NotBlank(message = "название события не пустое")
    String title;
    long views;
}
