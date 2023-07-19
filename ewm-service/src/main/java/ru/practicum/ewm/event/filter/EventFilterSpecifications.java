package ru.practicum.ewm.event.filter;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventFilterSpecifications {
    public static Specification<Event> getEventsAdminFilterSpecification(EventAdminFilter params) {
        return (event, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(params.getCategories())) {
                predicates.add(cb.in(event.get("category").get("id")).value(params.getCategories()));
            }
            if (Objects.nonNull(params.getStates())) {
                predicates.add(cb.in(event.get("state")).value(params.getStates()));
            }
            if (Objects.nonNull(params.getUsers())) {
                predicates.add(cb.in(event.get("initiator").get("id")).value(params.getUsers()));
            }
            if (Objects.nonNull(params.getRangeStart()) && Objects.nonNull(params.getRangeEnd())) {
                predicates.add(cb.between(event.get("eventDate"), params.getRangeStart(), params.getRangeEnd()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Event> getEventsFilterSpecification(EventFilter params) {
        return (event, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(params.getCategories())) {
                predicates.add(cb.in(event.get("category").get("id")).value(params.getCategories()));
            }
            if (Objects.nonNull(params.getPaid())) {
                predicates.add(cb.equal(event.get("paid"), params.getPaid()));
            }
            if (params.isOnlyAvailable()) {
                predicates.add(cb.equal(event.get("isAvailable"), true));
            }
            if (Objects.nonNull(params.getRangeStart()) && Objects.nonNull(params.getRangeEnd())) {
                predicates.add(cb.between(event.get("eventDate"), params.getRangeStart(), params.getRangeEnd()));
            } else {
                predicates.add(cb.greaterThan(event.get("eventDate"), LocalDateTime.now()));
            }
            if (Objects.nonNull(params.getText())) {
                Predicate annotationSearch = cb.like(cb.lower(event.get("annotation")), "%" + params.getText().toLowerCase() + "%");
                Predicate descriptionSearch = cb.like(cb.lower(event.get("description")), "%" + params.getText().toLowerCase() + "%");
                predicates.add(cb.or(annotationSearch, descriptionSearch));
            }
            if (Objects.nonNull(params.getSort())) {
                if (params.getSort().equals(EventSort.EVENT_DATE)) {
                    query.orderBy(cb.asc(event.get("eventDate")));
                } else if ((params.getSort().equals(EventSort.VIEWS))) {
                    query.orderBy(cb.desc(event.get("views")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };
    }
}
