package com.trinet.harness.domain;

import java.time.LocalDate;

public class Employee {
	private Integer id;
	private String name;
	private LocalDate birthDate;
	private String department;
	private Integer salary;
	public Employee(Integer i, String name, LocalDate birthDate, String department, Integer salary) {
		super();
		this.id = i;
		this.name = name;
		this.birthDate = birthDate;
		this.department = department;
		this.salary = salary;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public Integer getSalary() {
		return salary;
	}
	public void setSalary(Integer salary) {
		this.salary = salary;
	}
	
	
}
