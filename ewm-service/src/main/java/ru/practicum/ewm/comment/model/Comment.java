package ru.practicum.ewm.comment.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Table(name = "comments", schema = "public")
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String text;

    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id")
    User author;

    @ManyToOne
    @JoinColumn(name = "event", referencedColumnName = "id")
    Event event;

    @Column
    CommentStatus state;

    @Column(name = "updated_on", nullable = false, insertable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()")
    @CreationTimestamp
    LocalDateTime updatedOn;

    @Column(name = "created_on", nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()")
    @CreationTimestamp
    LocalDateTime createdOn;

}
