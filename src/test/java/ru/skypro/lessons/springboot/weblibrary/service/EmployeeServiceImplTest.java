package ru.skypro.lessons.springboot.weblibrary.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.model.Department;
import ru.skypro.lessons.springboot.weblibrary.model.Employee;
import ru.skypro.lessons.springboot.weblibrary.model.Position;
import ru.skypro.lessons.springboot.weblibrary.model.Report;
import ru.skypro.lessons.springboot.weblibrary.repository.DepartmentRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.ReportRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepositoryMock;
    @Mock
    private PositionRepository positionRepositoryMock;
    @Mock
    private DepartmentRepository departmentRepositoryMock;
    @Mock
    private ReportRepository reportRepositoryMock;
    @Mock
    private ObjectMapper objectMapperMock;
    private EmployeeService employeeService;
    private Position position;
    private Department department;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeServiceImpl(
                employeeRepositoryMock, positionRepositoryMock, departmentRepositoryMock,
                reportRepositoryMock, objectMapperMock
        );
        position = new Position(1L, "Boss");
        position.setName("Manager");
        department = new Department("Finance");
        department.setName("Finance");
    }

    private List<Employee> createTestEmployees() {
        Employee employee1 = new Employee(1L, "Anna", 5000, position, department);
        Employee employee2 = new Employee(2L, "Vladimir", 4000, position, department);
        return Arrays.asList(employee1, employee2);
    }

    @DisplayName("Тест метода получения всех сотрудников")
    @Test
    public void testGetAllEmployees() {

        List<Employee> employees = createTestEmployees();

        when(employeeRepositoryMock.findAll()).thenReturn(employees);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        verify(employeeRepositoryMock, times(1)).findAll();

        for (int i = 0; i < employees.size(); i++) {
            Employee expectedEmployee = employees.get(i);
            EmployeeDTO actualEmployeeDTO = result.get(i);

            assertEquals(expectedEmployee.getName(), actualEmployeeDTO.getName());
            assertEquals(expectedEmployee.getSalary(), actualEmployeeDTO.getSalary());
            assertEquals(expectedEmployee.getDepartment().getName(), actualEmployeeDTO.getDepartmentName());
            assertEquals(expectedEmployee.getPosition().getName(), actualEmployeeDTO.getPositionName());
        }
    }

    @DisplayName("Тест для пустой БД")
    @Test
    public void testGetAllEmployeesWhenNoData() {

        when(employeeRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        verify(employeeRepositoryMock, times(1)).findAll();

        assertEquals(0, result.size());
    }

    @DisplayName("Тест для ошибки чтения из БД")
    @Test
    public void testGetAllEmployeesWhenDatabaseError() {

        when(employeeRepositoryMock.findAll()).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, employeeService::getAllEmployees);

        verify(employeeRepositoryMock, times(1)).findAll();
    }

    @DisplayName("Тест для метода получения сотрудника по id")
    @Test
    public void testGetEmployeeById() {
        // Arrange
        Long id = 1L;

        Employee employee = createTestEmployees().get(0);

        when(employeeRepositoryMock.findById(id)).thenReturn(Optional.of(employee));

        EmployeeDTO result = employeeService.getEmployeeById(id);

        verify(employeeRepositoryMock, times(1)).findById(id);

        assertEquals(employee.getName(), result.getName());
        assertEquals(employee.getSalary(), result.getSalary());
        assertEquals(employee.getDepartment().getName(), result.getDepartmentName());
        assertEquals(employee.getPosition().getName(), result.getPositionName());
    }

    @DisplayName("Тест для проверки несуществующего id")
    @Test
    public void testGetEmployeeByIdWhenNotFound() {

        Long id = 1L;

        when(employeeRepositoryMock.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById(id));

        verify(employeeRepositoryMock, times(1)).findById(id);
    }

    @DisplayName("Тест для метода получения сотрудника по должности")
    @ParameterizedTest
    @MethodSource("ru.skypro.lessons.springboot.weblibrary.data.PositionDataProvider#providePositions")
    public void testGetEmployeesByPosition(String position) {

        Position positionObj = new Position(1L, "Boss");
        positionObj.setName(position);

        Department department = new Department("Finance");
        department.setName("Finance");

        Employee employee1 = new Employee(1L, "Anna", 5000, positionObj, department);
        Employee employee2 = new Employee(2L, "Vladimir", 4000, positionObj, department);

        List<Employee> employees = Arrays.asList(employee1, employee2);

        if (position != null) {
            when(employeeRepositoryMock.findByPositionName(position)).thenReturn(employees);
        } else {
            when(employeeRepositoryMock.findAll()).thenReturn(employees);
        }

        List<EmployeeDTO> result = employeeService.getEmployeesByPosition(position);

        if (position != null) {
            verify(employeeRepositoryMock, times(1)).findByPositionName(position);
        } else {
            verify(employeeRepositoryMock, times(1)).findAll();
        }

        for (int i = 0; i < employees.size(); i++) {
            Employee expectedEmployee = employees.get(i);
            EmployeeDTO actualEmployeeDTO = result.get(i);

            assertEquals(expectedEmployee.getName(), actualEmployeeDTO.getName());
            assertEquals(expectedEmployee.getSalary(), actualEmployeeDTO.getSalary());
            assertEquals(expectedEmployee.getDepartment().getName(), actualEmployeeDTO.getDepartmentName());
            assertEquals(expectedEmployee.getPosition().getName(), actualEmployeeDTO.getPositionName());
        }
    }

    @DisplayName("Тест для метода сохранения сотрудников в БД")
    @Test
    public void testAddEmployee() {

        List<EmployeeDTO> employeeDTOs = List.of(
                new EmployeeDTO("Anna", 5000, "Boss", "Finance"),
                new EmployeeDTO("Vladimir", 4000, "Manager", "Sales")
        );
        Position directorPosition = new Position(1L, "Boss");
        Position managerPosition = new Position(1L, "Boss");
        Department financeDepartment = new Department("Finance");
        Department salesDepartment = new Department("Sales");

        when(positionRepositoryMock.findByName("Boss")).thenReturn(Optional.of(directorPosition));
        when(positionRepositoryMock.findByName("Manager")).thenReturn(Optional.of(managerPosition));
        when(departmentRepositoryMock.findByName("Finance")).thenReturn(Optional.of(financeDepartment));
        when(departmentRepositoryMock.findByName("Sales")).thenReturn(Optional.of(salesDepartment));

        final List[] savedEmployees = new List[1];
        doAnswer(invocation -> {
            savedEmployees[0] = invocation.getArgument(0);
            return null;
        }).when(employeeRepositoryMock).saveAll(anyList());

        employeeService.addEmployee(employeeDTOs);

        assertEquals(2, savedEmployees[0].size());
    }

    @DisplayName("Тест для метода получения полной информации о сотруднике")
    @Test
    public void testGetEmployeeFullInfo() {

        Long id = 1L;

        Employee employee =createTestEmployees().get(0);

        Optional<Employee> employeeOptional = Optional.of(employee);

        when(employeeRepositoryMock.findEmployeeWithPosition(id)).thenReturn(employeeOptional);

        EmployeeDTO result = employeeService.getEmployeeFullInfo(id);

        assertNotNull(result);
        assertEquals("Anna", result.getName());
        assertEquals(5000, result.getSalary());
        assertEquals("Manager", result.getPositionName());
        assertEquals("Finance", result.getDepartmentName());
    }

    @DisplayName("Тест для метода редактирования данных сотрудника")
    @Test
    public void testEditEmployee() {

        Long id = 1L;

        EmployeeDTO employeeDTO = new EmployeeDTO("Anna", 5000, "Boss", "Finance");

        Employee existingEmployee = new Employee(id, "Vladimir", 4000, new Position(1L, "Boss"), new Department("Sales"));

        Position directorPosition = new Position(1L, "Boss");

        Department financeDepartment = new Department("Finance");

        when(employeeRepositoryMock.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(positionRepositoryMock.findByName("Boss")).thenReturn(Optional.of(directorPosition));
        when(departmentRepositoryMock.findByName("Finance")).thenReturn(Optional.of(financeDepartment));
        when(employeeRepositoryMock.save(existingEmployee)).thenReturn(existingEmployee);

        EmployeeDTO updatedEmployeeDTO = employeeService.editEmployee(id, employeeDTO);

        assertNotNull(updatedEmployeeDTO);
        assertEquals("Anna", existingEmployee.getName());
        assertEquals(5000, existingEmployee.getSalary());
        assertEquals(directorPosition, existingEmployee.getPosition());
        assertEquals(financeDepartment, existingEmployee.getDepartment());

        assertEquals(employeeDTO.getName(), updatedEmployeeDTO.getName());
    }

    @DisplayName("Тест для метода удаления сотрудника")
    @Test
    public void testDeleteEmployeeById() {

        Long id = 1L;
        Employee employee = createTestEmployees().get(1);

        when(employeeRepositoryMock.findById(id)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployeeById(id);

        verify(employeeRepositoryMock, times(1)).findById(id);
        verify(employeeRepositoryMock, times(1)).delete(employee);
    }

    @DisplayName("Тест для метода получения данных о сотрудниках с зарплатой выше заданной")
    @Test
    public void testGetEmployeesWithSalaryHigherThan() {
        // Arrange
        int compareSalary = 3000;
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "Anna", 5000, new Position(1L, "Boss"), new Department("Finance")));
        employees.add(new Employee(2L, "Vladimir", 4000, new Position(1L, "Boss"), new Department("Sales")));

        when(employeeRepositoryMock.findBySalaryGreaterThan(compareSalary)).thenReturn(employees);

        List<EmployeeDTO> result = employeeService.getEmployeesWithSalaryHigherThan(compareSalary);

        assertEquals(employees.size(), result.size());

        for (int i = 0; i < employees.size(); i++) {
            Employee expectedEmployee = employees.get(i);
            EmployeeDTO actualEmployeeDTO = result.get(i);

            assertEquals(expectedEmployee.getName(), actualEmployeeDTO.getName());
            assertEquals(expectedEmployee.getSalary(), actualEmployeeDTO.getSalary());
            assertEquals(expectedEmployee.getDepartment().getName(), actualEmployeeDTO.getDepartmentName());
            assertEquals(expectedEmployee.getPosition().getName(), actualEmployeeDTO.getPositionName());
        }
    }

    @DisplayName("Тест для метода получения данных о сотрудниках с самой высокой зарплатой")
    @Test
    public void testGetEmployeesWithHighestSalary() {

        List<Employee> employees = createTestEmployees();

        when(employeeRepositoryMock.getEmployeesWithHighestSalary()).thenReturn(employees);

        List<EmployeeDTO> result = employeeService.getEmployeesWithHighestSalary();

        assertEquals(2, result.size());
        for (int i = 0; i < employees.size(); i++) {
            Employee expectedEmployee = employees.get(i);
            EmployeeDTO actualEmployeeDTO = result.get(i);

            assertEquals(expectedEmployee.getName(), actualEmployeeDTO.getName());
            assertEquals(expectedEmployee.getSalary(), actualEmployeeDTO.getSalary());
            assertEquals(expectedEmployee.getDepartment().getName(), actualEmployeeDTO.getDepartmentName());
            assertEquals(expectedEmployee.getPosition().getName(), actualEmployeeDTO.getPositionName());
        }
    }

    @DisplayName("Тест для метода постраничного вывода данных о сотрудниках")
    @Test
    public void testGetEmployeesByPage() {

        int page = 0;
        List<Employee> employees = createTestEmployees();
        Pageable pageable = PageRequest.of(page, 5);
        Page<Employee> employeePage = new PageImpl<>(employees, pageable, employees.size());

        when(employeeRepositoryMock.findAll(pageable)).thenReturn(employeePage);

        Page<EmployeeDTO> result = employeeService.getEmployeesByPage(page);

        assertEquals(2, result.getContent().size());

        for (int i = 0; i < employees.size(); i++) {
            Employee expectedEmployee = employees.get(i);
            EmployeeDTO actualEmployeeDTO = result.getContent().get(i);

            assertEquals(expectedEmployee.getName(), actualEmployeeDTO.getName());
            assertEquals(expectedEmployee.getSalary(), actualEmployeeDTO.getSalary());
            assertEquals(expectedEmployee.getDepartment().getName(), actualEmployeeDTO.getDepartmentName());
            assertEquals(expectedEmployee.getPosition().getName(), actualEmployeeDTO.getPositionName());
        }
    }

    @DisplayName("Тест метода получения отчёта из БД по его id")
    @Test
    public void testGetReportById() {

        Long reportId = 1L;
        Report mockReport = new Report(reportId, "Report.json", "Report content");

        when(reportRepositoryMock.findById(reportId)).thenReturn(Optional.of(mockReport));

        Optional<Report> result = employeeService.getReportById(reportId);

        assertTrue(result.isPresent());
        assertEquals(mockReport, result.get());
    }
}