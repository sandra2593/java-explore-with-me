package ru.practicum.stats.server.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String app;

    @Column(nullable = false)
    String uri;

    @Column(nullable = false)
    String ip;

    @Column(nullable = false)
    LocalDateTime timestamp;
}
