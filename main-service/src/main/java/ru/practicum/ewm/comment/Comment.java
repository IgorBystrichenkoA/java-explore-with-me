package ru.practicum.ewm.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@NamedEntityGraph(name = "comment.graph", attributeNodes = {
        @NamedAttributeNode("event"),
        @NamedAttributeNode("author")
})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 7000, nullable = false)
    private String text;
    @Column(nullable = false)
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    public Comment(User author, String text, Event event) {
        this.author = author;
        this.text = text;
        this.event = event;
        this.created = LocalDateTime.now();
    }
}