package ru.skypro.lessons.springboot.weblibrary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.dto.ReportDTO;
import ru.skypro.lessons.springboot.weblibrary.model.Department;
import ru.skypro.lessons.springboot.weblibrary.model.Employee;
import ru.skypro.lessons.springboot.weblibrary.model.Position;
import ru.skypro.lessons.springboot.weblibrary.model.Report;
import ru.skypro.lessons.springboot.weblibrary.repository.DepartmentRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.ReportRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    private final DepartmentRepository departmentRepository;

    private final ReportRepository reportRepository;

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);


    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PositionRepository positionRepository,
                               DepartmentRepository departmentRepository, ReportRepository reportRepository,
                               ObjectMapper objectMapper) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        logger.info("Вызван метод getAllEmployees");
        Iterable<Employee> employees = employeeRepository.findAll();
        logger.debug("Обращение к Employee выполнено: " + employees);
        return StreamSupport.stream(employees.spliterator(), false)
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<EmployeeDTO> getEmployeesByPosition(String position) {
        logger.info("Вызван метод getEmployeesByPosition: " + position);
        List<Employee> employees;
        if (position != null) {
            employees = employeeRepository.findByPositionName(position);
        } else {
            employees = employeeRepository.findAll();
        }
        logger.debug("Обращение к Employee выполнено: " + employees);
        return employees.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        logger.info("Вызван метод getEmployeeById: " + id);
        Employee employee = employeeRepository.findById(id).orElseThrow();
        logger.debug("Обращение к Employee выполнено: " + employee);
        return convertToDto(employee);
    }

    @Override
    public void addEmployee(List<EmployeeDTO> employeeDTO) {
        logger.info("Вызван метод addEmployee: " + employeeDTO);
        List<Employee> employees = employeeDTO.stream()
                .map(this::convertToEntity)
                .toList();
        employeeRepository.saveAll(employees);
        logger.debug("Обращение к Employee выполнено: " + employees);
    }

    @Override
    public EmployeeDTO getEmployeeFullInfo(Long id) {
        logger.info("Вызван метод getEmployeeFullInfo: " + id);
        Optional<Employee> employeeOptional = employeeRepository.findEmployeeWithPosition(id);
        logger.debug("Обращение к Employee выполнено: " + employeeOptional);
        Employee employee = employeeOptional.orElseThrow();
        return convertToDto(employee);
    }


    @Override
    public EmployeeDTO editEmployee(Long id, EmployeeDTO employeeDTO) {
        logger.info("Вызван метод editEmployee:" + id + employeeDTO);
        Employee existingEmployee = employeeRepository.findById(id).orElseThrow();
        existingEmployee.setName(employeeDTO.getName());
        existingEmployee.setSalary(employeeDTO.getSalary());
        if (employeeDTO.getPositionName() != null) {
            Position position = positionRepository.findByName(employeeDTO.getPositionName()).orElseThrow();
            logger.debug("Обращение к Position выполнено: " + position);
            existingEmployee.setPosition(position);
        }
        if (employeeDTO.getDepartmentName() != null) {
            Department department = departmentRepository.findByName(employeeDTO.getDepartmentName()).orElseThrow();
            logger.debug("Обращение к Department выполнено: " + department);
            existingEmployee.setDepartment(department);
        }
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        logger.debug("Обращение к Employee выполнено: " + updatedEmployee);
        return convertToDto(updatedEmployee);

    }


    @Override
    public void deleteEmployeeById(Long id) {
        logger.info("Вызван метод deleteEmployeeById: " + id);
        Employee employee = employeeRepository.findById(id).orElseThrow();
        logger.debug("Обращение к Employee выполнено: " + employee);
        employeeRepository.delete(employee);
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithSalaryHigherThan(int compareSalary) {
        logger.info("Вызван метод getEmployeesWithSalaryHigherThan: " + compareSalary);
        List<Employee> employees = employeeRepository.findBySalaryGreaterThan(compareSalary);
        logger.debug("Обращение к Employee выполнено: " + employees);
        return employees.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithHighestSalary() {
        logger.info("Вызван метод getEmployeesWithHighestSalary");
        List<Employee> employees = employeeRepository.getEmployeesWithHighestSalary();
        logger.debug("Обращение к Employee выполнено: " + employees);
        return employees.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Page<EmployeeDTO> getEmployeesByPage(int page) {
        logger.info("Вызван метод getEmployeesByPage: " + page);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);
        logger.debug("Обращение к Employee выполнено: " + employeePage);
        return employeePage.map(this::convertToDto);
    }

    @Override
    public List<EmployeeDTO> loadEmployeesFromFile(MultipartFile file) throws IOException {
        logger.info("Вызван метод loadEmployeesFromFile: " + file);
        String jsonContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        return objectMapper.readValue(jsonContent, new TypeReference<>() {
        });
    }

    @Override
    public Long getReportByDepartment() throws IOException {
        logger.info("Вызван метод getReportByDepartment");
        List<Object[]> results = employeeRepository.getReportByDepartment();
        logger.debug("Обращение к Employee выполнено: " + results);
        List<ReportDTO> reportDTOs = results.stream()
                .map(row -> {
                    ReportDTO reportDTO = new ReportDTO();
                    reportDTO.setDepartmentName((String) row[0]);
                    reportDTO.setEmployeeCount((Long) row[1]);
                    reportDTO.setMaxSalary((Integer) row[2]);
                    reportDTO.setMinSalary((Integer) row[3]);
                    reportDTO.setAverageSalary((BigDecimal) row[4]);
                    return reportDTO;
                })
                .toList();
        return saveReportDTOsToJsonAndInTable(reportDTOs);
    }

    @Override
    public void generateJsonFileFromReport(Long id) {
        logger.info("Вызван метод generateJsonFileFromReport: " + id);
        Report report = reportRepository.findById(id).orElseThrow();
        logger.debug("Обращение к Report выполнено: " + report);
        String content = report.getContent();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(report.getFileName()))) {
            writer.write(content);
        } catch (Exception e) {
            logger.error("Ошибка создания Json файла", e);
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> getReportResponseById(Long id) {
        logger.info("Вызван метод getReportResponseById: " + id);
        Optional<Report> reportOptional = getReportById(id);
        if (reportOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Report report = reportOptional.get();
        ByteArrayResource resource = new ByteArrayResource(report.getContent().getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getFileName())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private EmployeeDTO convertToDto(Employee employee) {
        logger.info("Вызван метод convertToDto: " + employee);
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setName(employee.getName());
        employeeDTO.setSalary(employee.getSalary());
        employeeDTO.setPositionName(employee.getPosition().getName());
        employeeDTO.setDepartmentName(employee.getDepartment().getName());
        return employeeDTO;
    }

    private Employee convertToEntity(EmployeeDTO employeeDTO) {
        logger.info("Вызван метод convertToEntity: " + employeeDTO);
        Employee employee = new Employee();
        Position position = positionRepository.findByName(employeeDTO.getPositionName()).orElseThrow();
        logger.debug("Обращение к Position выполнено: " + position);
        Department department = departmentRepository.findByName(employeeDTO.getDepartmentName()).orElseThrow();
        logger.debug("Обращение к Department выполнено: " + department);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setName(employeeDTO.getName());
        employee.setSalary(employeeDTO.getSalary());
        return employee;
    }

    public Long saveReportDTOsToJsonAndInTable(List<ReportDTO> reportDTOs) throws IOException {
        logger.info("Вызван метод saveReportDTOsToJsonAndInTable: " + reportDTOs);
        Report report = null;
        String fileName = "report";
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmm");
        String formattedDateTime = currentDateTime.format(formatter);
        String fileNameWithDateTime = fileName + "_" + formattedDateTime;
        String json = objectMapper.writeValueAsString(reportDTOs);
        Path filePath = Files.write(Path.of(fileNameWithDateTime), json.getBytes());
        String fileContent = null;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            fileContent = reader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            logger.error("Ошибка чтения Json файла: ", e);

        }
        report = new Report();
        report.setFileName(fileNameWithDateTime);
        report.setContent(fileContent);
        reportRepository.save(report);
        logger.debug("Обращение к Report выполнено: " + report);
        return report.getId();
    }

    public Optional<Report> getReportById(Long id) {

        logger.info("Был вызван метод getReportById: " + id);
        Optional<Report> report = reportRepository.findById(id);
        logger.debug("Обращение к БД Report успешно: " + report);
        return report;
    }


}

