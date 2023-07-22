package ru.practicum.ewm.comment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.filter.CommentAdminFilter;
import ru.practicum.ewm.comment.filter.CommentFilterSpecifications;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentStatus;
import ru.practicum.ewm.comment.storage.CommentStorageDb;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventStatus;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService implements CommentServiceIntf {
    private final CommentStorageDb commentStorage;
    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public CommentService(CommentStorageDb commentStorage, EventService eventService, UserService userService) {
        this.commentStorage = commentStorage;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public CommentShortDto getCommentByIdPublic(long commentId) {
        Optional<Comment> comment = commentStorage.findCommentByIdAndState(commentId, CommentStatus.PUBLISHED);
        if (comment.isPresent()) {
            return CommentMapper.toCommentShortDto(comment.get());
        } else {
            throw new NotFoundException(String.format("нет комментария с id ", commentId));
        }
    }

    @Override
    @Transactional
    public List<CommentShortDto> getAllEventComments(long eventId, Pageable pageable) {
        Event event = EventMapper.fromEventFullDto(eventService.getEventById(eventId));

        return commentStorage.findCommentByEventAndStateOrderByCreatedOnDesc(event, CommentStatus.PUBLISHED, pageable)
                .stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentFullDto> getUserEventComments(long userId, long eventId, Pageable pageable) {
        User author = UserMapper.fromUserDto(userService.getUserById(userId));
        Event event = EventMapper.fromEventFullDto(eventService.getEventById(eventId));

        return commentStorage.findCommentsByAuthorAndEventOrderByCreatedOnDesc(author, event, pageable)
                .stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }


    @Override
    public List<CommentFullDto> getAllUserComments(long userId) {
        User author = UserMapper.fromUserDto(userService.getUserById(userId));

        return commentStorage.findCommentsByAuthorOrderByCreatedOnDesc(author).stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }

    @Override
    public CommentFullDto addUserComment(long userId, long eventId, CommentDto commentDto) {
        User author = UserMapper.fromUserDto(userService.getUserById(userId));
        Event event = EventMapper.fromEventFullDto(eventService.getEventById(eventId));

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new CantCommentBadStatusException("нельзя комментировать неопубликованное событие");
        }

        if (commentDto.getText().length() < 2 || commentDto.getText().length() > 2000) {
            throw new CantCommentLengthException("поле text >= 2 && <= 2000, текущее: " + commentDto.getText().length());
        }

        Comment comment = CommentMapper.fromCommentDto(commentDto);
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setState(CommentStatus.PUBLISHED);

        return CommentMapper.toCommentFullDto(commentStorage.save(comment));
    }

    @Override
    public void deleteUserComment(long userId, long commentId) {
        User author = UserMapper.fromUserDto(userService.getUserById(userId));
        Optional<Comment> comment = commentStorage.findCommentByIdAndAuthor(commentId, author);

        if (comment.isPresent()) {
            commentStorage.delete(comment.get());
        } else {
            throw new CantDeleteCommentException("нельзя этот удалить комментарий");
        }
    }

    @Override
    public CommentFullDto updateUserComment(long userId, long commentId, CommentDto updateComment) {
        Comment commentToUpdate = CommentMapper.fromCommentFullDto(getCommentById(commentId));

        if (!commentToUpdate.getAuthor().equals(UserMapper.fromUserDto(userService.getUserById(userId)))) {
            throw new CantCommentException("нельзя обновить чужой комментарий");
        }

        if (updateComment.getText().length() < 2 || updateComment.getText().length() > 2000) {
            throw new CantCommentLengthException("поле text >= 2 && <= 2000, текущее: " + updateComment.getText().length());
        }

        commentToUpdate.setText(updateComment.getText());
        commentToUpdate.setUpdatedOn(LocalDateTime.now());
        commentToUpdate.setState(CommentStatus.PUBLISHED);

        return CommentMapper.toCommentFullDto(commentStorage.save(commentToUpdate));
    }

    @Override
    public CommentFullDto complainUserComment(long userId, long commentId) {
        User author = UserMapper.fromUserDto(userService.getUserById(userId));
        Comment commentToUpdate = CommentMapper.fromCommentFullDto(getCommentById(commentId));

        if (commentToUpdate.getAuthor().equals(author)) {
            throw new CantCommentException("нельзя пожаловаться на свой комментарий");
        }

        commentToUpdate.setUpdatedOn(LocalDateTime.now());
        commentToUpdate.setState(CommentStatus.COMPLAINED_AT);

        return CommentMapper.toCommentFullDto(commentStorage.save(commentToUpdate));
    }

    @Override
    public List<CommentFullDto> getAllComments(CommentAdminFilter params, Pageable pageable) {
        Specification<Comment> specification = CommentFilterSpecifications.getCommentsAdminFilterSpecification(params);
        return commentStorage.findAll(specification, pageable).stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(long commentId) {
        commentStorage.delete(CommentMapper.fromCommentFullDto(getCommentById(commentId)));
    }

    @Override
    public List<CommentFullDto> getAllCommentsToReview(Pageable pageable) {
        return commentStorage.findCommentByStateOrderByCreatedOnDesc(CommentStatus.COMPLAINED_AT, pageable).stream()
                .map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }

    @Override
    public CommentFullDto updateCommentStatus(long commentId) {
        Comment commentToUpdate = CommentMapper.fromCommentFullDto(getCommentById(commentId));

        if (commentToUpdate.getState().equals(CommentStatus.COMPLAINED_AT)) {
            commentToUpdate.setState(CommentStatus.PUBLISHED);
        } else {
            throw new CantCommentBadStatusException("на этот комментарий не жаловались");
        }

        return CommentMapper.toCommentFullDto(commentStorage.save(commentToUpdate));
    }

    private CommentFullDto getCommentById(long commentId) {
        Optional<Comment> comment = commentStorage.findById(commentId);
        if (comment.isPresent()) {
            return CommentMapper.toCommentFullDto(comment.get());
        } else {
            throw new NotFoundException(String.format("нет комментария с id", commentId));
        }
    }
}
