package ru.skypro.lessons.springboot.weblibrary.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ReportDTO implements Serializable {

    private String departmentName;

    private Long employeeCount;

    private int maxSalary;

    private int minSalary;

    private BigDecimal averageSalary;

    public ReportDTO() {}
    public String getDepartmentName() {return departmentName;}

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getEmployeeCount() {return employeeCount;}
    public int getMaxSalary() {return maxSalary;}

    public void setEmployeeCount(Long employeeCount) {
        this.employeeCount = employeeCount;
    }

    public void setMaxSalary(int maxSalary) {
        this.maxSalary = maxSalary;
    }
    public int getMinSalary() {return minSalary;}

    public void setMinSalary(int minSalary) {
        this.minSalary = minSalary;
    }
    public BigDecimal getAverageSalary() {return averageSalary;}

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    @Override
    public String toString() {
        return "ReportDTO{" +
                "departmentName='" + departmentName + '\'' +
                ", employeeCount=" + employeeCount +
                ", maxSalary=" + maxSalary +
                ", minSalary=" + minSalary +
                ", averageSalary=" + averageSalary +
                '}';
    }
}
