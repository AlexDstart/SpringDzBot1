package ru.skypro.lessons.springboot.weblibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.lessons.springboot.weblibrary.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
