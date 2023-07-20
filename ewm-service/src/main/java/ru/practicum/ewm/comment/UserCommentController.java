package ru.practicum.ewm.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserCommentController {
    private final CommentService commentService;

    @Autowired
    public UserCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

//    получение всех комментариев пользователя
    @GetMapping("/{userId}/comments")
    public List<CommentFullDto> getAll(@PathVariable long userId) {
        return commentService.getAllUserComments(userId);
    }

//    получение всех комментариев пользователя к событию
    @GetMapping("/{userId}/comments/{eventId}")
    public List<CommentFullDto> getUserEventComments(@PathVariable long userId, @PathVariable long eventId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        return commentService.getUserEventComments(userId, eventId, PageRequest.of(from, size));
    }

    //    создание нового комментария
    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto add(@PathVariable long userId, @RequestParam long eventId, @Valid @RequestBody CommentDto newCommentDto) {
        return commentService.addUserComment(userId, eventId, newCommentDto);
    }

//    удаление своего комментария
    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId, @PathVariable long commentId) {
        commentService.deleteUserComment(userId, commentId);
    }

//    изменение своего комментария
    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentFullDto update(@PathVariable long userId, @PathVariable long commentId, @Valid @RequestBody CommentDto updateComment) {
        return commentService.updateUserComment(userId, commentId, updateComment);
    }

//    пожаловаться на комментарий
    @PatchMapping("/{userId}/comments")
    public CommentFullDto complain(@PathVariable long userId, @RequestParam long commentId) {
        return commentService.complainUserComment(userId, commentId);
    }
}
