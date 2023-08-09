package ru.skypro.lessons.springboot.weblibrary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.dto.PositionDTO;
import ru.skypro.lessons.springboot.weblibrary.service.EmployeeService;
import ru.skypro.lessons.springboot.weblibrary.service.PositionService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PositionService positionService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, PositionService positionService) {
        this.employeeService = employeeService;
        this.positionService = positionService;
    }

    @PostMapping("/")
    public ResponseEntity<String> addEmployee(@RequestBody List<EmployeeDTO> employeeDTO) {
        employeeService.addEmployee(employeeDTO);
        return ResponseEntity.ok("Команда выполнена успешно");
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> editEmployee(@PathVariable Long id, @RequestBody EmployeeDTO newEmployeeDTO) {
        return ResponseEntity.ok(employeeService.editEmployee(id, newEmployeeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable Long id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok("Команда выполнена успешно");
    }

    @GetMapping("/salary/highest")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithHighestSalary() {
        return ResponseEntity.ok(employeeService.getEmployeesWithHighestSalary());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> loadEmployeesFromFileAndSave(
            @RequestParam("file") MultipartFile file) throws IOException {
        employeeService.addEmployee(employeeService.loadEmployeesFromFile(file));
        return ResponseEntity.ok("Команда выполнена успешно");
    }

    @PostMapping("/report")
    public ResponseEntity<String> getReportByDepartment() throws IOException {
        Long reportId = employeeService.getReportByDepartment();
        return ResponseEntity.ok("Идентификатор (id) сохраненного отчёта: " + reportId);
    }


    @GetMapping("/{id}/fullInfo")
    public ResponseEntity<EmployeeDTO> getEmployeeFullInfo(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeFullInfo(id));
    }

    @GetMapping("/positions/all")
    public ResponseEntity<List<PositionDTO>> getAllPositions() {
        return ResponseEntity.ok(positionService.getAllPositions());
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/salary/higherThan")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithSalaryHigherThan(
            @RequestParam("compareSalary") int compareSalary) {
        return ResponseEntity.ok(employeeService.getEmployeesWithSalaryHigherThan(compareSalary));
    }

    @GetMapping("/position")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByPosition(
            @RequestParam(name = "position", required = false) String position) {
        return ResponseEntity.ok(employeeService.getEmployeesByPosition(position));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<EmployeeDTO>> getEmployeesByPage(
            @RequestParam(value = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok(employeeService.getEmployeesByPage(page));
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<ByteArrayResource> getReportByIdAndDownload(@PathVariable Long id) throws IOException {
        employeeService.generateJsonFileFromReport(id);
        return ResponseEntity.ok(employeeService.getReportResponseById(id).getBody());
    }
}
