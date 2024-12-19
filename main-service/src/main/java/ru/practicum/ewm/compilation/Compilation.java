package ru.practicum.ewm.compilation;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(name = "compilation.graph", attributeNodes = {
        @NamedAttributeNode("events")
})
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Boolean pinned;
    private String title;
    @ManyToMany
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    @Builder.Default
    private List<Event> events = List.of();
}
