package ru.skypro.lessons.springboot.weblibrary.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skypro.lessons.springboot.weblibrary.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByPositionName(String positionName);

    List<Employee> findBySalaryGreaterThan(int compareSalary);
    @Query(value = "SELECT * FROM employee " +
            "WHERE salary = (SELECT MAX(salary) " +
            "FROM employee)",
            nativeQuery = true)
    List<Employee> getEmployeesWithHighestSalary();

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.position LEFT JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findEmployeeWithPosition(@Param("id") Long id);

    Page<Employee> findAll(Pageable pageable);

    @Query(value = "SELECT d.name AS departmentName, " +
            "COUNT(*) AS employeeCount, " +
            "MAX(e.salary) AS maxSalary, " +
            "MIN(e.salary) AS minSalary, " +
            "AVG(e.salary) AS averageSalary " +
            "FROM employee e " +
            "JOIN department d ON e.department_id = d.id " +
            "GROUP BY departmentName",
            nativeQuery = true)
    List<Object[]> getReportByDepartment();


}