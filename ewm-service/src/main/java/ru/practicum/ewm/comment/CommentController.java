package ru.practicum.ewm.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

//    получение всех комментариев события eventId
    @GetMapping
    public List<CommentShortDto> getAll(@RequestParam long eventId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        return commentService.getAllEventComments(eventId, PageRequest.of(from, size));
    }

//    запросить комментарий
    @GetMapping("/{commentId}")
    public CommentShortDto getCommentById(@PathVariable long commentId) {
        return commentService.getCommentByIdPublic(commentId);
    }
}
