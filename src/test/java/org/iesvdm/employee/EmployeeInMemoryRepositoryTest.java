package org.iesvdm.employee;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test doubles that are "fakes" must be tested
 *
 *
 */
public class EmployeeInMemoryRepositoryTest {

	private EmployeeInMemoryRepository employeeRepository;

	private List<Employee> employees;

	@BeforeEach
	public void setup() {
		employees = new ArrayList<>();
		employeeRepository = new EmployeeInMemoryRepository(employees);
	}

	/**
	 * Descripcion del test:
	 * crea 2 Employee diferentes
	 * aniadelos a la coleccion de employees
	 * comprueba que cuando llamas a employeeRepository.findAll
	 * obtienes los empleados aniadidos en el paso anterior
	 */
	@Test
	public void testEmployeeRepositoryFindAll() {
		// Crea una lista de empleados
		List<Employee> employees = new ArrayList<>();
		// Crea dos empleados diferentes
		Employee employee1 = new Employee("1", 1000);
		Employee employee2 = new Employee("2", 2000);
		// Añade los empleados a la lista
		employees.add(employee1);
		employees.add(employee2);

		// Crea un objeto EmployeeRepository
		EmployeeRepository employeeRepository = new EmployeeInMemoryRepository(employees);

		// Llama al método findAll de EmployeeRepository
		List<Employee> foundEmployees = employeeRepository.findAll();

		// Comprueba que la lista devuelta contiene los empleados añadidos
		assertTrue(foundEmployees.contains(employee1));
		assertTrue(foundEmployees.contains(employee2));
	}

	/**
	 * Descripcion del test:
	 * salva un Employee mediante el metodo
	 * employeeRepository.save y comprueba que la coleccion
	 * employees contiene solo ese Employee
	 */
	@Test
	public void testEmployeeRepositorySaveNewEmployee() {
		// Crea una lista de empleados
		List<Employee> employees = new ArrayList<>();

		// Crea un objeto EmployeeRepository
		EmployeeRepository employeeRepository = new EmployeeInMemoryRepository(employees);

		// Crea un nuevo empleado
		Employee newEmployee = new Employee("3", 3000);

		// Salva el nuevo empleado utilizando el método save de EmployeeRepository
		employeeRepository.save(newEmployee);

		// Verifica que la colección de empleados ahora contiene solo este nuevo empleado
		assertEquals(1, employees.size());
		assertTrue(employees.contains(newEmployee));
	}

	/**
	 * Descripcion del tets:
	 * crea un par de Employee diferentes
	 * aniadelos a la coleccion de employees.
	 * A continuacion, mediante employeeRepository.save
	 * salva los Employee anteriores (mismo id) con cambios
	 * en el salario y comprueba que la coleccion employees
	 * los contiene actualizados.
	 */
	@Test
	public void testEmployeeRepositorySaveExistingEmployee() {
		//Crea una lista de empleados
		List<Employee> employees = new ArrayList<>();

		// Crea dos empleados diferentes
		Employee employee1 = new Employee("1", 1000);
		Employee employee2 = new Employee("2", 2000);
		// Añade los empleados a la lista
		employees.add(employee1);
		employees.add(employee2);

		// Crea un objeto EmployeeRepository
		EmployeeRepository employeeRepository = new EmployeeInMemoryRepository(employees);

		// Modifica los empleados existentes
		Employee modifiedEmployee1 = new Employee("1", 1500); // Cambia el salario de employee1
		Employee modifiedEmployee2 = new Employee("2", 2500); // Cambia el salario de employee2

		// Salva los empleados modificados utilizando el método save de EmployeeRepository
		employeeRepository.save(modifiedEmployee1);
		employeeRepository.save(modifiedEmployee2);

		// Verifica que la colección de empleados ahora contiene los empleados modificados
		assertTrue(employees.contains(modifiedEmployee1));
		assertTrue(employees.contains(modifiedEmployee2));
	}
}
