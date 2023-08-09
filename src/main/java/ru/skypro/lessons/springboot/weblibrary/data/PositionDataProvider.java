package ru.skypro.lessons.springboot.weblibrary.data;

import java.util.stream.Stream;

public class PositionDataProvider {
    public static Stream<String> providePositions() {
        return Stream.of("Programmer", "Engineer", "Analyst", null);
    }
}
