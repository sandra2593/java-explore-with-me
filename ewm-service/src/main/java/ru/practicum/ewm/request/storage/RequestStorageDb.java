package ru.practicum.ewm.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface RequestStorageDb extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester(User user);

    @Query(
            "SELECT new Request(req.id, req.created, req.event, req.requester, req.status) " +
                    "FROM Request req " +
                    "JOIN Event e " +
                    "ON e.id = req.event.id " +
                    "WHERE e.initiator.id = :initiatorId AND e.id = :eventId"
    )
    List<Request> findAllUserEventRequests(long initiatorId, long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    @Query("SELECT COUNT(*) from Request req WHERE req.event = :event AND req.status = :status")
    long findCountOfConfirmedRequests(Event event, RequestStatus status);

}
