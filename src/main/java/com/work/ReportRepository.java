package com.work;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByProjectIdAndEmployeeIdAndDate(Long projectId, Long employeeId, LocalDate date);
    int countByProjectIdAndEmployeeIdAndDate(Long projectId, Long employeeId, LocalDate date);
}
