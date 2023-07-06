package ru.practicum.stats.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    @NotBlank(message = "Название приложения не пустое")
    String app;
    @NotBlank(message = "URI не пустая")
    String uri;
    @NotBlank(message = "IP-адрес не пустой")
    String ip;
    @NotNull(message = "Дата посещения не пустая")
    LocalDateTime timestamp;
}
