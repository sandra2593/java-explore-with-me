package ru.practicum.ewm.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.filter.CommentAdminFilter;

import java.util.List;

public interface CommentServiceIntf {
    List<CommentShortDto> getAllEventComments(long eventId, Pageable pageable);

    List<CommentFullDto> getUserEventComments(long userId, long eventId, Pageable pageable);

    CommentShortDto getCommentByIdPublic(long commentId);

    List<CommentFullDto> getAllUserComments(long userId);

    CommentFullDto addUserComment(long userId, long eventId, CommentDto commentDto);

    void deleteUserComment(long userId, long commentId);

    CommentFullDto updateUserComment(long userId, long commentId, CommentDto updateComment);

    CommentFullDto complainUserComment(long userId, long commentId);

    List<CommentFullDto> getAllComments(CommentAdminFilter params, Pageable pageable);

    void deleteComment(long commentId);

    List<CommentFullDto> getAllCommentsToReview(Pageable pageable);

    CommentFullDto updateCommentStatus(long commentId);
}
