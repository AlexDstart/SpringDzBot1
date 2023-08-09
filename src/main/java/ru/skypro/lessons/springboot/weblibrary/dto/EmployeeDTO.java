package ru.skypro.lessons.springboot.weblibrary.dto;

public class EmployeeDTO {
    private String name;
    private int salary;
    private String positionName;
    private String departmentName;

    public EmployeeDTO() {
    }

    public EmployeeDTO(String name, int salary, String positionName, String departmentName) {
        this.name = name;
        this.salary = salary;
        this.positionName = positionName;
        this.departmentName = departmentName;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "name='" + name + '\'' +
                ", salary=" + salary +
                ", positionName='" + positionName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
