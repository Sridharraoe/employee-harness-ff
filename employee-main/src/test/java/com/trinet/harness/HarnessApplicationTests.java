package com.trinet.harness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trinet.harness.domain.Employee;
import com.trinet.harness.domain.FFRedisDto;
import com.trinet.harness.service.EmployeeService;
import com.trinet.harness.service.FeatureFlagsService;
import com.trinet.harness.utils.EmployeeUtils;
import com.trinet.harness.utils.FeatureFlagConstants;
import com.trinet.harness.utils.HarnessProvider;

import io.harness.cf.client.api.FeatureFlagInitializeException;
import io.micrometer.common.util.StringUtils;

@SpringBootTest
class HarnessApplicationTests {
	Logger logger = LoggerFactory.getLogger(CfClientConfiguration.class);

	static Employee newEmployee;

	static List<Employee> employeeList;

	@Autowired
	HarnessProvider hrnProvider;

	@Autowired
	EmployeeService employeeService;

	@InjectMocks
	EmployeeService employeeServiceInject;

	@Mock
	HarnessProvider harnessProvider;

	@Mock
	EmployeeService employeeServiceMock;

	@BeforeAll
	static void contextLoads() {
		newEmployee = new Employee(1, "Ramesh", LocalDate.of(1990, 2, 1), "HR", 12000);

		employeeList = new ArrayList<>();
		employeeList.add(new Employee(1, "Ramesh", LocalDate.of(1990, 2, 1), "HR", 12000));
		employeeList.add(new Employee(2, "Rajesh", LocalDate.of(1991, 5, 14), "HR", 15000));
		employeeList.add(new Employee(3, "Sahil", LocalDate.of(1993, 2, 23), "SALES", 12000));
		employeeList.add(new Employee(4, "Shekhar", LocalDate.of(1990, 2, 24), "SALES", 15000));
		employeeList.add(new Employee(5, "James", LocalDate.of(1990, 2, 15), "IT", 12000));
		employeeList.add(new Employee(6, "Rosy", LocalDate.of(1993, 2, 15), "IT", 15000));

	}

	@Test
	void testAddEmployee() {
		int beforeSize = EmployeeUtils.employeeList.size();
		employeeService.save(newEmployee);
		int afterSize = EmployeeUtils.employeeList.size();
		assertEquals(beforeSize + 1, afterSize);
	}

	@Test
	void deleteEmployee() {
		int beforeSize = EmployeeUtils.employeeList.size();
		employeeService.delete(1);
		int afterSize = EmployeeUtils.employeeList.size();
		assertEquals(beforeSize - 1, afterSize);
	}

	@Test
	void displayEmployee() throws InterruptedException, FeatureFlagInitializeException {
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.IT_DEPARTMENT_FLAG)).thenReturn(true);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.ALL_DEPARTMENT_FLAG)).thenReturn(true);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.HR_DEPARTMENT_FLAG)).thenReturn(false);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.SALES_DEPARTMENT_FLAG)).thenReturn(false);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.EMPLOYEE_DISPLAY_API)).thenReturn(true);
		List<Employee> empList = employeeServiceInject.getEmployees();
		int size = empList.size();
		int actualSize = employeeList.size();

		assertEquals(size, actualSize);
	}

	@Test
	void displayEmployeeAPIEnabledFlagTest() throws InterruptedException, FeatureFlagInitializeException {
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.IT_DEPARTMENT_FLAG)).thenReturn(true);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.ALL_DEPARTMENT_FLAG)).thenReturn(false);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.HR_DEPARTMENT_FLAG)).thenReturn(false);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.SALES_DEPARTMENT_FLAG)).thenReturn(false);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.EMPLOYEE_DISPLAY_API)).thenReturn(true);
		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.ALL_DEPARTMENT_FLAG)).thenReturn(true);

		int size = employeeServiceInject.getEmployees().size();
		int actualSize = EmployeeUtils.employeeList.size();

		assertEquals(size, actualSize);

	}

//	@Test
//	void displayITDepartmentTest() throws InterruptedException, FeatureFlagInitializeException {
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.IT_DEPARTMENT_FLAG)).thenReturn(true);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.ALL_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.HR_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.SALES_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.EMPLOYEE_DISPLAY_API)).thenReturn(true);
//		List<Employee> empList = employeeServiceInject.getEmployees();
//		int size = empList.size();
//		int actualSize = employeeList.stream().filter(e -> e.getDepartment().equals(FeatureFlagConstants.IT_DEPARTMENT))
//				.toList().size();
//		;
//		assertEquals(size, actualSize);
//	}
//
//	@Test
//	void displayHRDepartmentTest() throws InterruptedException, FeatureFlagInitializeException {
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.IT_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.ALL_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.HR_DEPARTMENT_FLAG)).thenReturn(true);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.SALES_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.EMPLOYEE_DISPLAY_API)).thenReturn(true);
//		List<Employee> empList = employeeServiceInject.getEmployees();
//		int size = empList.size();
//		int actualSize = employeeList.stream().filter(e -> e.getDepartment().equals(FeatureFlagConstants.HR_DEPARTMENT))
//				.toList().size();
//		;
//		assertEquals(size, actualSize);
//	}
//
//	@Test
//	void displaySalesDepartmentTest() throws InterruptedException, FeatureFlagInitializeException {
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.IT_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.ALL_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.HR_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.SALES_DEPARTMENT_FLAG)).thenReturn(true);
//		when(harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.EMPLOYEE_DISPLAY_API)).thenReturn(true);
//		List<Employee> empList = employeeServiceInject.getEmployees();
////		boolean ffRedDist = hrnProvider.getFlagValues("employee_display_api");
////		boolean testFailure = hrnProvider.getFlagValues("testFlag02");
////		logger.info("test-variation "+ffRedDist);
////		logger.info("testFlag02 "+testFailure);
//		int size = empList.size();
////		if (ffRedDist && !testFailure) {
////			logger.info("tTest failes");
////			size = empList.size() + 1;
////		}
//
//		int actualSize = employeeList.stream()
//				.filter(e -> e.getDepartment().equals(FeatureFlagConstants.SALES_DEPARTMENT)).toList().size();
//		;
//		assertEquals(size, actualSize);
//	}

//	@Test
//	 void testExceptionMethod() throws InterruptedException, FeatureFlagInitializeException {
//		when(harnessProvider.getFlagValues(FeatureFlagConstants.IT_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValues(FeatureFlagConstants.ALL_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValues(FeatureFlagConstants.HR_DEPARTMENT_FLAG)).thenReturn(false);
//		when(harnessProvider.getFlagValues(FeatureFlagConstants.SALES_DEPARTMENT_FLAG)).thenReturn(true);
//		when(harnessProvider.getFlagValues(FeatureFlagConstants.EMPLOYEE_DISPLAY_API)).thenReturn(false);
//		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//			employeeServiceInject.getEmployees();
//		});
//		String expectedMessage = FeatureFlagConstants.API_DISABLED_MESSAGE;
//		String actualMessage = exception.getMessage();
//		assertTrue(actualMessage.contains(expectedMessage));
//
//	}
}
