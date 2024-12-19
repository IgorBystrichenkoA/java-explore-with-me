package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UpdateEventRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminEventParam {

    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    @JsonSetter(nulls = Nulls.SKIP)
    private Integer from = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    private Integer size = 10;

    private Long eventId;

    private UpdateEventRequest updateEventRequest;

    public Predicate toPredicate() {
        BooleanBuilder booleanBuilder = new BooleanBuilder(Expressions.TRUE);
        if (users != null && !users.isEmpty()) {
            BooleanExpression byUsers = QEvent.event.initiator.id.in(users);
            booleanBuilder.and(byUsers);
        }
        if (states != null && !states.isEmpty()) {
            BooleanExpression byStates = QEvent.event.state.in(states);
            booleanBuilder.and(byStates);
        }
        if (categories != null && !categories.isEmpty()) {
            BooleanExpression byCategories = QEvent.event.category.id.in(categories);
            booleanBuilder.and(byCategories);
        }
        if (rangeStart != null) {
            BooleanExpression byRangeStart = QEvent.event.eventDate.after(rangeStart);
            booleanBuilder.and(byRangeStart);
        }
        if (rangeEnd != null) {
            BooleanExpression byRangeEnd = QEvent.event.eventDate.before(rangeEnd);
            booleanBuilder.and(byRangeEnd);
        }
        return booleanBuilder.getValue();
    }
}
