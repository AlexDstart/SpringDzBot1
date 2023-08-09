package ru.skypro.lessons.springboot.weblibrary.dto;

public class PositionDTO {

    private String positionName;

    public PositionDTO() {
    }

    public PositionDTO(String positionName) {
        this.positionName = positionName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
}