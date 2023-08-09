package ru.skypro.lessons.springboot.weblibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.dto.PositionDTO;
import ru.skypro.lessons.springboot.weblibrary.model.Department;
import ru.skypro.lessons.springboot.weblibrary.model.Employee;
import ru.skypro.lessons.springboot.weblibrary.model.Position;
import ru.skypro.lessons.springboot.weblibrary.model.Report;
import ru.skypro.lessons.springboot.weblibrary.repository.DepartmentRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.ReportRepository;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EmployeeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PositionRepository positionRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    public void resetDatabase() {
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @DisplayName("Добавление новых сотрудников успешно")
    @Test
    @SneakyThrows
    void addNewEmployeesIsSuccess() {
        Position position1 = new Position("Manager");
        Position position2 = new Position("Boss");
        Department department1 = new Department("Sales");
        Department department2 = new Department("Finance");
        List<EmployeeDTO> employeeDTOs = List.of(
                new EmployeeDTO("Anna", 5000, "Boss", "Finance"),
                new EmployeeDTO("Vladimir", 4000, "Manager", "Sales")
        );
        positionRepository.save(position1);
        positionRepository.save(position2);
        departmentRepository.save(department1);
        departmentRepository.save(department2);

        String jsonEmployees = new ObjectMapper().writeValueAsString(employeeDTOs);

        mockMvc.perform(post("/employees/")
                        .with(user("user_admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEmployees))
                .andExpect(status().isOk())
                .andExpect(content().string("Команда выполнена успешно"));
    }

    @DisplayName("Добавление новых сотрудников не успешно")
    @Test
    @SneakyThrows
    void addNewEmployeesIsNotSuccess() {
        List<EmployeeDTO> employeeDTOs = List.of(
                new EmployeeDTO("Anna", 5000, "Boss", "Finance"),
                new EmployeeDTO("Vladimir", 4000, "Manager", "Sales")
        );

        String jsonEmployees = new ObjectMapper().writeValueAsString(employeeDTOs);

        mockMvc.perform(post("/employees/")
                        .with(user("user_admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEmployees))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("Изменение сотрудника успешно")
    @Test
    @SneakyThrows
    void editEmployeeIsSuccess() {
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        Employee employee = new Employee("Vladimir", 4000, position, department);
        positionRepository.save(position);
        departmentRepository.save(department);
        employeeRepository.save(employee);
        Long employeeId = employee.getId();
        EmployeeDTO employeeDtoForUpdate = new EmployeeDTO("Andrey", 2000, "Manager", "Sales");

        String jsonEmployee = new ObjectMapper().writeValueAsString(employeeDtoForUpdate);

        mockMvc.perform(put("/employees/{id}", employeeId)
                        .with(user("user_admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEmployee))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Andrey"))
                .andExpect(jsonPath("$.salary").value(2000))
                .andExpect(jsonPath("$.positionName").value("Manager"))
                .andExpect(jsonPath("$.departmentName").value("Sales"));
    }

    @DisplayName("Изменение сотрудника не успешно")
    @Test
    @SneakyThrows
    void editEmployeeIsNotSuccess() {
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        positionRepository.save(position);
        departmentRepository.save(department);
        Long employeeId = 999L;
        EmployeeDTO employeeDtoForUpdate = new EmployeeDTO("Andrey", 2000, "Manager", "Sales");

        String jsonEmployee = new ObjectMapper().writeValueAsString(employeeDtoForUpdate);

        mockMvc.perform(put("/employees/{id}", employeeId)
                        .with(user("user_admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEmployee))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("Удаление сотрудника по id успешно")
    @Test
    @SneakyThrows
    void deleteEmployeeByValidIdIsSuccess() {
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        Employee employee = new Employee("Vladimir", 4000, position, department);
        positionRepository.save(position);
        departmentRepository.save(department);
        employeeRepository.save(employee);
        Long employeeId = employee.getId();

        mockMvc.perform(delete("/employees/{id}", employeeId)
                        .with(user("user_admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Команда выполнена успешно"));
    }

    @DisplayName("Удаление сотрудника по несуществующему id не успешно")
    @Test
    @SneakyThrows
    void deleteEmployeeByNotValidIdIsNotSuccess() {
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        Employee employee = new Employee("Vladimir", 4000, position, department);
        positionRepository.save(position);
        departmentRepository.save(department);
        employeeRepository.save(employee);
        Long employeeId = 999L;

        mockMvc.perform(delete("/employees/{id}", employeeId)
                        .with(user("user_admin").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("Получение сотрудников с максимальной зарплатой")
    @Test
    @SneakyThrows
    void getEmployeesWithHighestSalary() {
        Position position1 = new Position("Manager");
        Position position2 = new Position("Boss");
        Department department1 = new Department("Sales");
        Department department2 = new Department("Finance");
        Employee employee1 = new Employee("Anna", 10000, position2, department2);
        Employee employee2 = new Employee("Vladimir", 10000, position2, department2);
        Employee employee3 = new Employee("Maria", 2000, position1, department1);

        positionRepository.save(position1);
        positionRepository.save(position2);
        departmentRepository.save(department1);
        departmentRepository.save(department2);
        employeeRepository.saveAll(List.of(employee1, employee2, employee3));

        List<EmployeeDTO> expectedEmployees = List.of(convertEmployeeToDto(employee1), convertEmployeeToDto(employee2));

        String jsonEmployees = new ObjectMapper().writeValueAsString(expectedEmployees);

        mockMvc.perform(get("/employees/salary/highest")
                        .with(user("user_test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonEmployees));
    }

    @DisplayName("Тест для rest-запроса по загрузке employees из json-файла")
    @Test
    @SneakyThrows
    void loadEmployeesFromFileAndSaveTest() {
        Position position = new Position("Manager");
        positionRepository.save(position);
        Department department = new Department("Sales");
        departmentRepository.save(department);

        MockMultipartFile file = new MockMultipartFile("file", "employees.json", "application/json",
                ("[{\"name\":\"John Doe\",\"salary\":5000,\"positionName\":\"Manager\",\"departmentName\":\"Sales\"}," +
                        "{\"name\":\"John Rick\",\"salary\":7000,\"positionName\":\"Manager\",\"departmentName\":\"Sales\"}]").getBytes());

        mockMvc.perform(multipart("/employees/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(user("user_admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain;charset=UTF-8"));
    }

    @DisplayName("Получение сотрудников с максимальной зарплатой")
    @Test
    @SneakyThrows
    void getReportByDepartment() {
        long reportId = 1L;
        mockMvc.perform(post("/employees/report")
                        .with(user("user_admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Идентификатор (id) сохраненного отчёта: " + reportId));
    }

    @DisplayName("Получение полной информации о сотруднике")
    @Test
    @SneakyThrows
    void getEmployeeFullInfo() {
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        Employee employee = new Employee("Anna", 5000, position, department);
        positionRepository.save(position);
        departmentRepository.save(department);
        employeeRepository.save(employee);
        Long employeeId = employee.getId();

        mockMvc.perform(get("/employees/{id}/fullInfo", employeeId)
                        .with(user("user_test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.name").value("Anna"))
                .andExpect(jsonPath("$.salary").value(5000))
                .andExpect(jsonPath("$.positionName").value("Manager"))
                .andExpect(jsonPath("$.departmentName").value("Sales"));
    }

    @DisplayName("Получение всех должностей")
    @Test
    @SneakyThrows
    void getAllPositions() {
        Position position1 = new Position("Manager");
        Position position2 = new Position("Boss");
        Position position3 = new Position("Developer");
        positionRepository.save(position1);
        positionRepository.save(position2);
        positionRepository.save(position3);

        List<PositionDTO> expectPositionDtoList = List.of(
                convertPositionToDto(position1),
                convertPositionToDto(position2),
                convertPositionToDto(position3)
        );
        String jsonPositions = new ObjectMapper().writeValueAsString(expectPositionDtoList);

        mockMvc.perform(get("/employees/positions/all")
                        .with(user("user_test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(content().json(jsonPositions));
    }

    @DisplayName("Получение всех сотрудников")
    @Test
    @SneakyThrows
    void getAllEmployees() {
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        positionRepository.save(position);
        departmentRepository.save(department);
        Employee employee1 = new Employee("Anna", 5000, position, department);
        Employee employee2 = new Employee("Vladimir", 10000, position, department);
        Employee employee3 = new Employee("Maria", 2000, position, department);
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);

        List<EmployeeDTO> expectEmployeeDtoList = List.of(convertEmployeeToDto(employee1), convertEmployeeToDto(employee2),
                convertEmployeeToDto(employee3)
        );
        String jsonEmployees = new ObjectMapper().writeValueAsString(expectEmployeeDtoList);

        mockMvc.perform(get("/employees/all")
                        .with(user("user_test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(content().json(jsonEmployees));
    }

    @DisplayName("Получение сотрудника по корректному id успешно")
    @Test
    @SneakyThrows
    void getEmployeeByValidIdIsSuccess() {
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        positionRepository.save(position);
        departmentRepository.save(department);
        Employee employee = new Employee("Anna", 5000, position, department);
        employeeRepository.save(employee);
        Long employeeId = employee.getId();

        EmployeeDTO expectEmployee = convertEmployeeToDto(employee);
        String jsonEmployee = new ObjectMapper().writeValueAsString(expectEmployee);

        mockMvc.perform(get("/employees/{id}", employeeId)
                        .with(user("user_test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(content().json(jsonEmployee));
    }

    @DisplayName("Получение сотрудника по некорректному id  не успешно")
    @Test
    @SneakyThrows
    void getEmployeeByInvalidIdIsNotSuccess() {
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        positionRepository.save(position);
        departmentRepository.save(department);
        Long employeeId = 999L;

        mockMvc.perform(get("/employees/{id}", employeeId)
                        .with(user("user_test").roles("USER")))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("Получение всех сотрудников у которых зарплата выше чем 10000")
    @Test
    @SneakyThrows
    void getEmployeesWithSalaryHigherThan() {
        Integer compareSalary = 10000;
        Position position = new Position("Manager");
        Department department = new Department("Sales");
        positionRepository.save(position);
        departmentRepository.save(department);
        Employee employee1 = new Employee("Anna", 12000, position, department);
        Employee employee2 = new Employee("Vladimir", 12000, position, department);
        Employee employee3 = new Employee("Maria", 2000, position, department);
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);

        List<EmployeeDTO> expectEmployeeDtoList = List.of(convertEmployeeToDto(employee1), convertEmployeeToDto(employee2));
        String jsonEmployees = new ObjectMapper().writeValueAsString(expectEmployeeDtoList);

        mockMvc.perform(get("/employees/salary/higherThan")
                        .with(user("user_test").roles("USER"))
                        .param("compareSalary", String.valueOf(compareSalary)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(content().json(jsonEmployees));
    }

    @DisplayName("Получение всех сотрудников по должности")
    @Test
    @SneakyThrows
    void getEmployeesByPosition() {
        Position position1 = new Position("Manager");
        Position position2 = new Position("Boss");
        Department department = new Department("Sales");
        positionRepository.save(position1);
        positionRepository.save(position2);
        departmentRepository.save(department);
        Employee employee1 = new Employee("Anna", 12000, position1, department);
        Employee employee2 = new Employee("Vladimir", 12000, position1, department);
        Employee employee3 = new Employee("Maria", 2000, position2, department);
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);

        List<EmployeeDTO> expectEmployeeDtoList = List.of(convertEmployeeToDto(employee1), convertEmployeeToDto(employee2));
        String jsonEmployees = new ObjectMapper().writeValueAsString(expectEmployeeDtoList);

        mockMvc.perform(get("/employees/position")
                        .with(user("user_test").roles("USER"))
                        .param("position", position1.getName()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(content().json(jsonEmployees));
    }

    @DisplayName("Получение всех сотрудников по странице")
    @Test
    @SneakyThrows
    void getEmployeesByPage() {
        int page = 0;
        Position position1 = new Position("Manager");
        Position position2 = new Position("Boss");
        Department department = new Department("Sales");
        positionRepository.save(position1);
        positionRepository.save(position2);
        departmentRepository.save(department);
        Employee employee1 = new Employee("Anna", 12000, position1, department);
        Employee employee2 = new Employee("Vladimir", 12000, position1, department);
        Employee employee3 = new Employee("Maria", 2000, position2, department);
        Employee employee4 = new Employee("Petr", 12000, position1, department);
        Employee employee5 = new Employee("Alexandr", 2000, position2, department);
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);
        employeeRepository.save(employee4);
        employeeRepository.save(employee5);

        mockMvc.perform(get("/employees/page")
                        .with(user("user_test").roles("USER"))
                        .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @DisplayName("Получение и загрузка отчета по id")
    @Test
    @SneakyThrows
    void getReportByIdAndDownload() {
        Report report = new Report("fileName", "content");
        reportRepository.save(report);
        Long reportId = report.getId();

        mockMvc.perform(get("/employees/report/{id}", reportId)
                        .with(user("user_test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    private EmployeeDTO convertEmployeeToDto(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setName(employee.getName());
        employeeDTO.setSalary(employee.getSalary());
        employeeDTO.setPositionName(employee.getPosition().getName());
        employeeDTO.setDepartmentName(employee.getDepartment().getName());
        return employeeDTO;
    }

    private PositionDTO convertPositionToDto(Position position) {
        PositionDTO positionDTO = new PositionDTO();
        positionDTO.setPositionName(position.getName());
        return positionDTO;
    }

}

