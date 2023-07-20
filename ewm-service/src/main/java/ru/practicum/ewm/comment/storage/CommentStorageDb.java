package ru.practicum.ewm.comment.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentStatus;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

public interface CommentStorageDb extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    Optional<Comment> findCommentByIdAndState(long commentId, CommentStatus state);

    List<Comment> findCommentByEventAndStateOrderByCreatedOnDesc(Event event, CommentStatus commentState, Pageable pageable);

    List<Comment> findCommentsByAuthorOrderByCreatedOnDesc(User author);

    Optional<Comment> findCommentByIdAndAuthor(long commentId, User author);

    List<Comment> findCommentsByAuthorAndEventOrderByCreatedOnDesc(User author, Event event, Pageable pageable);

    List<Comment> findCommentByStateOrderByCreatedOnDesc(CommentStatus commentState, Pageable pageable);

}
