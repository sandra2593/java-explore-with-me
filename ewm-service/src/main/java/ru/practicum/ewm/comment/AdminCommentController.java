package ru.practicum.ewm.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.filter.CommentAdminFilter;
import ru.practicum.ewm.comment.model.CommentStatus;
import ru.practicum.ewm.comment.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @Autowired
    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

//    просмотр комментариев
    @GetMapping
    public List<CommentFullDto> getAllComments(@RequestParam(required = false) List<Long> users, @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) List<CommentStatus> commentStates, @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart, @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {

        CommentAdminFilter params = CommentAdminFilter.builder().users(users).events(events).commentStates(commentStates)
                .rangeStart(rangeStart).rangeEnd(rangeEnd).build();

        return commentService.getAllComments(params, PageRequest.of(from, size));
    }

//    просмотр комментариев, на которые пожаловались
    @GetMapping("/review")
    public List<CommentFullDto> getAllCommentsToReview(@RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        return commentService.getAllCommentsToReview(PageRequest.of(from, size));
    }

//    удалить комментарий
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }

//    обновление статуса для комментариев, на которые пожаловались
    @PatchMapping("/{commentId}")
    public CommentFullDto updateCommentStatus(@PathVariable long commentId) {
        return commentService.updateCommentStatus(commentId);
    }
}
