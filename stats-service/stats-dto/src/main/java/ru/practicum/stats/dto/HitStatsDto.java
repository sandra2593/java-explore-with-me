package ru.practicum.stats.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class HitStatsDto {
    @NotBlank(message = "Название приложения не пустое")
    String app;
    @NotBlank(message = "URI не пустая")
    String uri;
    Long hits;
}
