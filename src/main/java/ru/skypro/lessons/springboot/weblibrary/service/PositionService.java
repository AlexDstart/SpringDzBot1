package ru.skypro.lessons.springboot.weblibrary.service;


import ru.skypro.lessons.springboot.weblibrary.dto.PositionDTO;

import java.util.List;

public interface PositionService {
    void addPosition(PositionDTO positionDTO);

    List<PositionDTO> getAllPositions();
}
