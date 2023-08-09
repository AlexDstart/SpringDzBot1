package ru.skypro.lessons.springboot.weblibrary.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.lessons.springboot.weblibrary.model.Position;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByName(String positionName);
}
