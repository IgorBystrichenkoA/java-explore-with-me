package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.dto.CountByEvent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findRequestByRequesterIdAndEventId(Long userId, Long eventId);

    Optional<Request> getRequestById(Long requestId);

    @Query("SELECT new ru.practicum.ewm.event.dto.CountByEvent(r.event.id, COUNT(r.id))" +
            " FROM Request r" +
            " WHERE r.event.id IN (?1) AND r.status = ?2" +
            " GROUP BY r.event.id")
    List<CountByEvent> countByStatusRequests(List<Long> eventIds, RequestStatus status);

    List<Request> getRequestsByRequesterId(Long userId);

    List<Request> getRequestsByEventId(Long eventId);

    List<Request> findByIdIn(Collection<Long> ids);
}
