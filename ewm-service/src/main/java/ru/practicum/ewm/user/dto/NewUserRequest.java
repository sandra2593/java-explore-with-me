package ru.practicum.ewm.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @Email(message = "некорректный email")
    @NotBlank(message = "email не пустой")
    String email;
    @NotBlank(message = "имя user не пустое")
    String name;
}
