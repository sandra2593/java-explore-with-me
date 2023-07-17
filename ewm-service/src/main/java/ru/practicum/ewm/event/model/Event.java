package ru.practicum.ewm.event.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.Location;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "events", schema = "public")
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, length = 120)
    String title;

    @Column(nullable = false, length = 2000)
    String annotation;

    @ManyToOne
    @JoinColumn(name = "initiator", referencedColumnName = "id")
    User initiator;

    @ManyToOne
    @JoinColumn(name = "category", referencedColumnName = "id", nullable = false)
    Category category;

    @Column(nullable = false, length = 7000)
    String description;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @Column(name = "created_on", nullable = false, insertable = false, updatable = false)
    @CreationTimestamp
    LocalDateTime createdOn;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(nullable = false)
    @Embedded
    Location location;

    @Column
    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column
    EventStatus state = EventStatus.PENDING;

    @ManyToMany(mappedBy = "events")
    Set<Compilation> compilations;

    @Column(columnDefinition = "INT8 DEFAULT 0", insertable = false, nullable = false)
    Long confirmedRequests = 0L;

    @Column(columnDefinition = "INT8 DEFAULT 0", insertable = false, nullable = false)
    Long views = 0L;

    @Column(name = "is_available")
    boolean isAvailable = true;
}
