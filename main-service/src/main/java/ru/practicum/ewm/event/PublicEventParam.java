package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.validation.ValidationException;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.event.model.QEvent;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.model.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PublicEventParam {

    private String text;

    private List<Long> categories;

    private Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean onlyAvailable = false;

    @JsonSetter(nulls = Nulls.SKIP)
    private EventSort sort;

    @JsonSetter(nulls = Nulls.SKIP)
    private int from = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    private int size = 10;

    private Long eventId;

    private void isValidCategories() {
        if (categories != null && !categories.stream().allMatch(category -> category > 0)) {
            throw new ValidationException("Некорректный формат идентификатора категории");
        }
    }

    private void isValidDate() {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }
    }

    public Predicate toPredicate() {
        isValidCategories();
        isValidDate();

        BooleanExpression onlyPublished = QEvent.event.publishedOn.before(LocalDateTime.now());
        BooleanBuilder booleanBuilder = new BooleanBuilder(onlyPublished);
        booleanBuilder.and(Expressions.TRUE);
        if (text != null) {
            BooleanExpression byAnnotation = QEvent.event.annotation.containsIgnoreCase(text);
            BooleanExpression byDescription = QEvent.event.description.containsIgnoreCase(text);
            booleanBuilder.and(byAnnotation.or(byDescription));
        }
        if (categories != null && !categories.isEmpty()) {
            BooleanExpression byCategories = QEvent.event.category.id.in(categories);
            booleanBuilder.and(byCategories);
        }
        if (paid != null) {
            BooleanExpression byPaid = QEvent.event.paid.eq(paid);
            booleanBuilder.and(byPaid);
        }
        if (rangeStart == null && rangeEnd == null) {
            BooleanExpression afterCurrent = QEvent.event.eventDate.after(LocalDateTime.now());
            booleanBuilder.and(afterCurrent);
        }
        if (rangeStart != null) {
            BooleanExpression byRangeStart = QEvent.event.eventDate.after(rangeStart);
            booleanBuilder.and(byRangeStart);
        }
        if (rangeEnd != null) {
            BooleanExpression byRangeEnd = QEvent.event.eventDate.before(rangeEnd);
            booleanBuilder.and(byRangeEnd);
        }
        if (onlyAvailable) {
            BooleanExpression isAvailable = QEvent.event.confirmedRequests.size().lt(QEvent.event.participantLimit);
            booleanBuilder.and(isAvailable);
        }
        return booleanBuilder.getValue();
    }
}
