package com.trinet.harness.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trinet.harness.CfClientConfiguration;
import com.trinet.harness.domain.Employee;
import com.trinet.harness.service.EmployeeService;
import com.trinet.harness.service.GitHubActionsService;

@RestController
public class EmployeeController {

	Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	EmployeeService employeeService;

	@Autowired
	CfClientConfiguration cfClientConfiguration;
	
	@Autowired
	GitHubActionsService gitHubActionsService;


	@GetMapping("/employees")
	public ResponseEntity<List<Employee>> employees() {

		List<Employee> empList = employeeService.getEmployees();
		if (empList == null || empList.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(empList);
	}

	@PostMapping("/employees")
	public ResponseEntity<String> addEmployee(@RequestBody Employee emp) {

		employeeService.save(emp);

		return ResponseEntity.ok("created successfully");
	}

	@DeleteMapping("/employees/{id}")
	public ResponseEntity<String> deleteEmployee(@PathVariable("id") Integer id) {

		employeeService.delete(id);

		return ResponseEntity.ok("deleted successfully");
	}

	@GetMapping("/employees/workflow")
	public ResponseEntity<String> getWorkflowStatus(@RequestParam("status") String status) {
		logger.info("GITHUB ACTIONS WORKFLOW RESPONSE STATUS " + status);

		try {
			logger.info("====updating redis from getWorkflowStatus");
			cfClientConfiguration.getFFValues();

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return ResponseEntity.ok("Triggered");
	}
	
	@GetMapping("/employees/github")
	public ResponseEntity<String> github() {
		logger.info("GITHUB ACTIONS CALLED ");

		logger.info("====github triggerred");
		gitHubActionsService.triggerGitHubActionWorkflow();

		return ResponseEntity.ok("Triggered");
	}
	
		
	@PostMapping("/employees/webhook")
	public ResponseEntity<String> githubWebhook(@RequestBody String data) {
		logger.info("webhook received data : {}", data);
		return ResponseEntity.ok("Triggered");
	}

}
