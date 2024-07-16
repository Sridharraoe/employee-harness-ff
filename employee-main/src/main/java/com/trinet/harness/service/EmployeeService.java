package com.trinet.harness.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.trinet.harness.domain.Employee;
import com.trinet.harness.utils.EmployeeUtils;
import com.trinet.harness.utils.FeatureFlagConstants;
import com.trinet.harness.utils.HarnessProvider;

@Service
public class EmployeeService {

	Logger logger = LoggerFactory.getLogger(EmployeeService.class);

	HarnessProvider harnessProvider;

	public EmployeeService(HarnessProvider harnessProvider) {
		this.harnessProvider = harnessProvider;
	}

	public List<Employee> getEmployees() {
		Set<String> deptFilters = new HashSet<>();
		try {
			boolean isEmployeeAPIisEnabled =  true; //harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.EMPLOYEE_DISPLAY_API);
			logger.info("isEmployeeAPIisEnabled: {}", isEmployeeAPIisEnabled);

			if (!isEmployeeAPIisEnabled) {
				logger.info(FeatureFlagConstants.API_DISABLED_MESSAGE);
				throw new RuntimeException(FeatureFlagConstants.API_DISABLED_MESSAGE);
			}

			boolean isAllDeptEnabled = true; //harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.ALL_DEPARTMENT_FLAG);
			boolean isItDepartmentEnabled =  true; //harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.IT_DEPARTMENT_FLAG);
			boolean isHrDepartment = true; //harnessProvider.getFlagValuesFromCache(FeatureFlagConstants.HR_DEPARTMENT_FLAG);
			boolean isSalesDepartmentEnabled =  true; //harnessProvider
//					.getFlagValuesFromCache(FeatureFlagConstants.SALES_DEPARTMENT_FLAG);

			logger.info(
					"isAllDeptEnabled: {}, isItDepartmentEnabled: {}, isHrDepartment: {}, isSalesDepartmentEnabled: {}",
					isAllDeptEnabled, isItDepartmentEnabled, isHrDepartment, isSalesDepartmentEnabled);

			if (isAllDeptEnabled) {
				return EmployeeUtils.employeeList;
			}

			if (isItDepartmentEnabled) {
				deptFilters.add(FeatureFlagConstants.IT_DEPARTMENT);
			}
			if (isHrDepartment) {
				deptFilters.add(FeatureFlagConstants.HR_DEPARTMENT);
			}
			if (isSalesDepartmentEnabled) {
				deptFilters.add(FeatureFlagConstants.SALES_DEPARTMENT);
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return EmployeeUtils.getEmployeesList(deptFilters);
	}

	public void save(Employee emp) {

		EmployeeUtils.addEmployee(emp);

	}

	public void delete(Integer id) {
		EmployeeUtils.deleteEmployee(id);

	}


}
