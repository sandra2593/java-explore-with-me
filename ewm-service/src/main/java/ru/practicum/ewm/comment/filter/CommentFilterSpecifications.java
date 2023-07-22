package ru.practicum.ewm.comment.filter;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.comment.model.Comment;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class CommentFilterSpecifications {
    public static Specification<Comment> getCommentsAdminFilterSpecification(CommentAdminFilter params) {
        return (comment, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params.getUsers() != null) {
                predicates.add(cb.in(comment.get("author").get("id")).value(params.getUsers()));
            }

            if (params.getEvents() != null) {
                predicates.add(cb.in(comment.get("event").get("id")).value(params.getEvents()));
            }

            if (params.getCommentStates() != null) {
                predicates.add(cb.in(comment.get("state")).value(params.getCommentStates()));
            }

            if (params.getRangeStart() != null && params.getRangeEnd() != null) {
                predicates.add(cb.between(comment.get("updatedOn"), params.getRangeStart(), params.getRangeEnd()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
