package ru.skypro.lessons.springboot.weblibrary.service;

import org.springframework.stereotype.Service;
import ru.skypro.lessons.springboot.weblibrary.dto.PositionDTO;
import ru.skypro.lessons.springboot.weblibrary.model.Position;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;

    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public void addPosition(PositionDTO positionDTO) {
        Position position = convertToEntity(positionDTO);
        positionRepository.save(position);
    }

    @Override
    public List<PositionDTO> getAllPositions() {
        Iterable<Position> positions = positionRepository.findAll();
        return StreamSupport.stream(positions.spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PositionDTO convertToDto(Position position) {
        PositionDTO positionDTO = new PositionDTO();
        positionDTO.setPositionName(position.getName());
        return positionDTO;
    }

    private Position convertToEntity(PositionDTO positionDTO) {
        Position position = new Position();
        position.setName(positionDTO.getPositionName());
        return position;
    }
}
