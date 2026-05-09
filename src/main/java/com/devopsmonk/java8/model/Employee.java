package com.devopsmonk.java8.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Domain model used across all tutorial chapters.
 * Deliberately simple — no frameworks, no annotations — so the focus stays on Java 8 features.
 */
public class Employee {

    private final long id;
    private final String name;
    private final Department department;
    private final double salary;
    private final LocalDate joinDate;
    private final boolean active;

    public Employee(long id, String name, Department department, double salary,
                    LocalDate joinDate, boolean active) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.joinDate = joinDate;
        this.active = active;
    }

    // Convenience constructor — active by default
    public Employee(long id, String name, Department department, double salary, LocalDate joinDate) {
        this(id, name, department, salary, joinDate, true);
    }

    public long getId()              { return id; }
    public String getName()          { return name; }
    public Department getDepartment(){ return department; }
    public double getSalary()        { return salary; }
    public LocalDate getJoinDate()   { return joinDate; }
    public boolean isActive()        { return active; }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', dept=%s, salary=%.0f, joined=%s, active=%s}",
                id, name, department, salary, joinDate, active);
    }

    // -------------------------------------------------------------------------
    // Sample data used across all chapters — call SampleData.employees()
    // -------------------------------------------------------------------------

    public static class SampleData {

        public static List<Employee> employees() {
            return Arrays.asList(
                new Employee(1, "Alice Chen",     Department.ENGINEERING, 95000, LocalDate.of(2019, 3, 15)),
                new Employee(2, "Bob Martin",     Department.ENGINEERING, 88000, LocalDate.of(2020, 7, 1)),
                new Employee(3, "Carol Smith",    Department.PRODUCT,     82000, LocalDate.of(2018, 11, 20)),
                new Employee(4, "David Kim",      Department.ENGINEERING, 105000, LocalDate.of(2017, 5, 10)),
                new Employee(5, "Eve Johnson",    Department.DESIGN,      75000, LocalDate.of(2021, 2, 28)),
                new Employee(6, "Frank Lee",      Department.MARKETING,   68000, LocalDate.of(2022, 1, 3)),
                new Employee(7, "Grace Hopper",   Department.ENGINEERING, 115000, LocalDate.of(2016, 8, 22)),
                new Employee(8, "Hank Green",     Department.FINANCE,     79000, LocalDate.of(2020, 4, 14)),
                new Employee(9, "Iris Wang",      Department.PRODUCT,     87000, LocalDate.of(2019, 9, 5)),
                new Employee(10, "Jack Brown",    Department.HR,          62000, LocalDate.of(2023, 6, 1)),
                new Employee(11, "Karen Davis",   Department.SALES,       71000, LocalDate.of(2021, 10, 18)),
                new Employee(12, "Liam Wilson",   Department.ENGINEERING, 92000, LocalDate.of(2020, 12, 7)),
                new Employee(13, "Mia Taylor",    Department.DESIGN,      77000, LocalDate.of(2022, 3, 15)),
                new Employee(14, "Noah Anderson", Department.FINANCE,     83000, LocalDate.of(2018, 7, 30)),
                new Employee(15, "Olivia White",  Department.ENGINEERING, 98000, LocalDate.of(2019, 1, 12),  false),
                new Employee(16, "Paul Harris",   Department.MARKETING,   65000, LocalDate.of(2023, 9, 22),  false)
            );
        }
    }
}
