package ru.skypro.lessons.springboot.weblibrary.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.model.Report;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface EmployeeService {

        List<EmployeeDTO> getAllEmployees();

        List<EmployeeDTO> getEmployeesByPosition(String position);

        EmployeeDTO getEmployeeById(Long id);

        void addEmployee(List<EmployeeDTO> employeeDTO);

        EmployeeDTO editEmployee(Long id, EmployeeDTO employeeDTO);



        void deleteEmployeeById(Long id);

        List<EmployeeDTO> getEmployeesWithSalaryHigherThan(int compareSalary);

        List<EmployeeDTO> getEmployeesWithHighestSalary();

        EmployeeDTO getEmployeeFullInfo(Long id);

        Page<EmployeeDTO> getEmployeesByPage(int page);
        List<EmployeeDTO> loadEmployeesFromFile(MultipartFile file) throws IOException;

        Long getReportByDepartment() throws IOException, RuntimeException;

        void generateJsonFileFromReport(Long id) throws IOException, RuntimeException;

        Optional<Report> getReportById(Long id);

        ResponseEntity<ByteArrayResource> getReportResponseById(Long id);

    }

