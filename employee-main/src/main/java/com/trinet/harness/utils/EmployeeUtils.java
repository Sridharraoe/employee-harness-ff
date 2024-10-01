package com.trinet.harness.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.trinet.harness.domain.Employee;

public class EmployeeUtils {

	
	private EmployeeUtils() {
		
	}
	public static List<Employee> employeeList = new ArrayList<>();
	static {
		employeeList.add(new Employee(1, "GET /employees/{employeeId}", LocalDate.of(1990, 2, 1), "emp-microservice", 12000));
		employeeList.add(new Employee(2, "PUT /employees/{employeeId}", LocalDate.of(1991, 5, 14), "emp-microservice", 15000));
		employeeList.add(new Employee(3, "DELETE /employees/{employeeId}", LocalDate.of(1993, 2, 23), "emp-microservice", 12000));
		employeeList.add(new Employee(4, "GET /employees/", LocalDate.of(1990, 2, 24), "emp-microservice", 15000));
		//employeeList.add(new Employee(5, "James", LocalDate.of(1990, 2, 15), "IT", 12000));
		//employeeList.add(new Employee(6, "Rosy", LocalDate.of(1993, 2, 15), "IT", 15000));
	}

	public static List<Employee> getEmployeesList(Set<String> dept) {
		return employeeList.stream().filter(e -> dept.contains(e.getDepartment().toUpperCase())).toList();
	}

	public static void addEmployee(Employee emp) {
		employeeList.add(emp);
	}

	public static void deleteEmployee(Integer id) {
		employeeList.removeIf(e -> e.getId().equals(id));
	}

}
