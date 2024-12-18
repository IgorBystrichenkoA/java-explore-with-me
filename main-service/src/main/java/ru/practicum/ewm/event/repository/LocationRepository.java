package ru.practicum.ewm.event.repository;

import ru.practicum.ewm.event.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLatAndLon(Float lat, Float lon);
}
