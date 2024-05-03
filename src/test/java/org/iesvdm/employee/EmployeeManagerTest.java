package org.iesvdm.employee;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


import org.assertj.core.api.AssertJProxySetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EmployeeManagerTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private BankService bankService;

	/**
	 * Explica en este comentario que efecto tiene
	 * esta anotacion @InjectMocks
	 */
	@InjectMocks
	private EmployeeManager employeeManager;

	@Captor
	private ArgumentCaptor<String> idCaptor;

	@Captor
	private ArgumentCaptor<Double> amountCaptor;

	@Spy
	private Employee notToBePaid = new Employee("1", 1000);

	@Spy
	private Employee toBePaid = new Employee("2", 2000);

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion vacia.
	 * Comprueba que al invocar employeeManagar.payEmployees
	 * con el stub anterior no se paga a ningun empleado.
	 */
	@Test
	public void testPayEmployeesReturnZeroWhenNoEmployeesArePresent() {
		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva una colección vacía
		when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

		// Llama al método payEmployees del objeto employeeManager
		int payments = employeeManager.payEmployees();

		// Verifica que no se ha pagado a ningún empleado
		assertEquals(0, payments);
		// Verifica que no se haya llamado a bankService.pay
		verify(bankService, never()).pay(anyString(), anyDouble());
	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn que devuelva una lista con un empleado
	 * para employeeRepository.findAll.
	 * Comprueba que al invocar employeeManager.payEmployess con el stub
	 * anterior se paga a un empleado.
	 * Tambien comprueba con verify que se hace una llamada a bankService.pay
	 * con los datos de pago del Employ del stub when-thenReturn inicialmente
	 * creado.
	 */
	@Test
	public void testPayEmployeesReturnOneWhenOneEmployeeIsPresentAndBankServicePayPaysThatEmployee() {
		// Crea un empleado para simular que está presente en el repositorio
		Employee employee = new Employee("3", 3000);
		List<Employee> employees = Arrays.asList(employee);

		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva la lista con el empleado
		when(employeeRepository.findAll()).thenReturn(employees);

		// Llama al método payEmployees del objeto employeeManager
		int payments = employeeManager.payEmployees();

		// Verifica que se ha pagado a un empleado
		assertEquals(1, payments);

		// Verifica que se haya llamado a bankService.pay con los datos correctos del empleado
		verify(bankService, times(1)).pay(idCaptor.capture(), amountCaptor.capture());
		assertEquals(employee.getId(), idCaptor.getValue());
		assertEquals(employee.getSalary(), amountCaptor.getValue(), 0.01);
	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion con 2 empleados diferentes.
	 * Comprueba que employeeManager.payEmployees paga a 2 empleados.
	 * Verifica la interaccion (con verify) de que se hacen 2 invocaciones
	 * con las caracteristicas de pago de cada Employee que creaste en el stub
	 * primero when-thenReturn.
	 * Por último, verificea que no hay más interacciones con el mock de bankService
	 * -pista verifyNoiMoreInteractions.
	 */
	@Test
	public void testPayEmployeesWhenSeveralEmployeeArePresent() {
		// Crea una lista con dos empleados diferentes
		Employee employee1 = new Employee("3", 3000);
		Employee employee2 = new Employee("4", 4000);
		List<Employee> employees = Arrays.asList(employee1, employee2);

		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva la lista con los dos empleados
		when(employeeRepository.findAll()).thenReturn(employees);

		// Llama al método payEmployees del objeto employeeManager
		int payments = employeeManager.payEmployees();

		// Verifica que se han pagado a dos empleados
		assertEquals(2, payments);

		// Verifica que se hayan llamado a bankService.pay dos veces con los datos correctos de los empleados
		verify(bankService, times(2)).pay(idCaptor.capture(), amountCaptor.capture());
		List<String> capturedIds = idCaptor.getAllValues();
		List<Double> capturedAmounts = amountCaptor.getAllValues();
		assertEquals(2, capturedIds.size());
		assertEquals(2, capturedAmounts.size());
		assertEquals(employee1.getId(), capturedIds.get(0));
		assertEquals(employee1.getSalary(), capturedAmounts.get(0), 0.01);
		assertEquals(employee2.getId(), capturedIds.get(1));
		assertEquals(employee2.getSalary(), capturedAmounts.get(1), 0.01);

		// Verifica que no hay más interacciones con el mock de bankService
		verifyNoMoreInteractions(bankService);

	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion de 2 empleados.
	 * Comprueba que cuando llamas a employeeManager.payEmployee pagas a 2 empleados.
	 * Para el mock de bankService mediante InOrder e inOrder.verify verifica
	 * que se pagan en orden a los 2 empleados con sus caracteristicas invocando
	 * a pay en el orden de la coleccion.
	 * Por ultimo, verifica que despues de pagar no hay mas interacciones.
	 */
	@Test
	public void testPayEmployeesInOrderWhenSeveralEmployeeArePresent() {

	}

	/**
	 * Descripcion del test:
	 * Misma situacion que el test anterior solo que al inOrder le aniades tambien employeeRepository
	 * para verificar que antes de hacer el pago bankService.pay para cada empleado
	 * se realiza la invocacion de employeeRepository.findAll.
	 * Pista: utiliza un InOrder inOrder = inOrder(bankService, employeeRepository) para
	 * las verificaciones (verify).
	 */
	@Test
	public void testExampleOfInOrderWithTwoMocks() {
		// Crea una lista con dos empleados diferentes
		Employee employee1 = new Employee("3", 3000);
		Employee employee2 = new Employee("4", 4000);
		List<Employee> employees = Arrays.asList(employee1, employee2);

		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva la lista con los dos empleados
		when(employeeRepository.findAll()).thenReturn(employees);

		// Llama al método payEmployees del objeto employeeManager
		employeeManager.payEmployees();

		// Verifica que se hayan llamado a bankService.pay dos veces con los datos correctos de los empleados, en orden
		InOrder inOrder = inOrder(bankService);
		inOrder.verify(bankService).pay(employee1.getId(), employee1.getSalary());
		inOrder.verify(bankService).pay(employee2.getId(), employee2.getSalary());

		// Verifica que no hay más interacciones con el mock de bankService
		verifyNoMoreInteractions(bankService);
	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * una coleccion con 2 Employee diferentes. Comprueba que employeesManager.payEmployees paga
	 * a 2 Employee.
	 * Seguidamente utiliza los Captor: idCaptor y amountCaptor para capturar todos los
	 * id's y amounts que se han invocado cuando has comprobado que employManager.payEmployees pagaba a 2,
	 * sobre el mock de bankService en un verify para el metodo pay -puedes aniadir cuantas veces se invoco
	 * al metodo pay en el VerificationMode.
	 * Comprueba los valores de los captor accediendo a ellos mediante captor.getAllValues y comparando
	 * con lo que se espera.
	 * Por ultimo verifica que no hay mas interacciones con el mock de bankService.
	 */
	@Test
	public void testExampleOfArgumentCaptor() {
		// Crea una lista con dos empleados diferentes
		Employee employee1 = new Employee("3", 3000);
		Employee employee2 = new Employee("4", 4000);
		List<Employee> employees = Arrays.asList(employee1, employee2);

		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva la lista con los dos empleados
		when(employeeRepository.findAll()).thenReturn(employees);

		// Llama al método payEmployees del objeto employeeManager
		employeeManager.payEmployees();

		// Verifica que se hayan llamado a bankService.pay dos veces con los datos correctos de los empleados
		verify(bankService, times(2)).pay(idCaptor.capture(), amountCaptor.capture());

		// Obtiene todos los valores capturados por los captors
		List<String> capturedIds = idCaptor.getAllValues();
		List<Double> capturedAmounts = amountCaptor.getAllValues();

		// Comprueba los valores capturados
		assertEquals(2, capturedIds.size());
		assertEquals(2, capturedAmounts.size());
		assertEquals("3", capturedIds.get(0));
		assertEquals(3000.0, capturedAmounts.get(0), 0.01);
		assertEquals("4", capturedIds.get(1));
		assertEquals(4000.0, capturedAmounts.get(1), 0.01);

		// Verifica que no hay más interacciones con el mock de bankService
		verifyNoMoreInteractions(bankService);
	}

	/**
	 * Descripcion del test:
	 * Utiliza el spy toBePaid de los atributos de esta clase de test para
	 * crear un stub when-thenReturn con 1 solo Employee.
	 * Comprueba que al invocar a employeeManager.payEmployees solo paga a 1 Employee.
	 * Por ultimo, mediante un inOrder para 2 mocks: InOrder inOrder = inOrder(bankService, toBePaid)
	 * verifica que la interaccion se realiza en el orden de bankService.pay las caracteristicas
	 * del Employee toBePaid y a continuacion verifica tambien que se invoca toBePaid.setPaid true.
	 */
	@Test
	public void testEmployeeSetPaidIsCalledAfterPaying() {
		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva una lista con el Employee toBePaid
		when(employeeRepository.findAll()).thenReturn(Arrays.asList(toBePaid));

		// Llama al método payEmployees del objeto employeeManager
		employeeManager.payEmployees();

		// Verifica que se haya pagado solo a un empleado
		verify(bankService, times(1)).pay(anyString(), anyDouble());

		// Verifica que se haya llamado a toBePaid.setPaid(true) después de pagar al empleado
		InOrder inOrder = inOrder(bankService, toBePaid);
		inOrder.verify(bankService).pay(anyString(), anyDouble()); // Utiliza matchers para todos los argumentos
		inOrder.verify(toBePaid).setPaid(true);
	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * una coleccion solo con el spy de atributo de la clase notToBePaid.
	 * Seguidamente, crea un stub doThrow-when para bankService.pay con ArgumentMatcher
	 * any como entradas para el metodo pay. La exception a lanzar sera una RuntimeException
	 * Comprueba que cuando invocas employeeManager.payEmployees con bankService lanzando
	 * una RuntimeException en el stub anterior, los Employee pagados son 0.
	 * Tambien, verifica sobre el spy notToBePaid que se llamo a setPaid false como
	 * efecto de no pago.
	 *
	 */
	@Test
	public void testPayEmployeesWhenBankServiceThrowsException() {
		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva una lista con notToBePaid
		when(employeeRepository.findAll()).thenReturn(Arrays.asList(notToBePaid));

		// Crea un stub doThrow-when para bankService.pay con ArgumentMatcher any como entradas para el método pay
		doThrow(new RuntimeException()).when(bankService).pay(anyString(), anyDouble());

		// Llama al método payEmployees del objeto employeeManager
		int payments = employeeManager.payEmployees();

		// Verifica que no se haya pagado a ningún empleado
		assertEquals(0, payments);

		// Verifica que se haya llamado a setPaid(false) en el empleado notToBePaid
		verify(notToBePaid).setPaid(false);
	}

	/**
	 * Descripcion del test:
	 * 	Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * 	una coleccion 2 Employee con el spy de atributo de la clase notToBePaid y toBePaid.
	 * 	Seguidamente, crea un stub con encademaniento para 2 llamadas doThrow.doNothing-when
	 * 	para bankService.pay de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
	 * 	y en la segunda invocacion de pay (para toBePaid) no haga nada. El metodo pay acepta cualquier argumento
	 * 	indicado mediante ArgumentMatcher any.
	 * 	Comprueba que al invocar employeeManager.payEmployees se paga a solo 1 Employee.
	 *  A continuacion, verifica las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
	 *  para el que se lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion que si recibe el pago
	 *  chequeando la interaccion con el metodo setPaid a false y true respectivamente.
	 */
	@Test
	public void testOtherEmployeesArePaidWhenBankServiceThrowsException() {
		// Crea una lista con dos empleados diferentes
		Employee employee1 = mock(Employee.class);
		Employee employee2 = mock(Employee.class);
		List<Employee> employees = Arrays.asList(employee1, employee2, notToBePaid);

		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva la lista con los empleados
		when(employeeRepository.findAll()).thenReturn(employees);

		// Crea un stub doThrow-when para bankService.pay con ArgumentMatcher any como entradas para el método pay
		doThrow(new RuntimeException()).when(bankService).pay(anyString(), anyDouble());

		// Llama al método payEmployees del objeto employeeManager
		employeeManager.payEmployees();

		// Verifica que notToBePaid no se haya pagado
		verify(notToBePaid).setPaid(false);

		// Verifica que los otros empleados se hayan pagado correctamente
		for (Employee employee : employees) {
			if (employee != notToBePaid) {
				verify(employee).setPaid(true);
			}
		}
	}


	/**
	 * Descripcion del test:
	 * 	Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * 	una coleccion 2 Employee con el spy de atributo de la clase notToBePaid y toBePaid.
	 * 	Seguidamente, crea un stub con encademaniento para 2 llamadas doThrow-when emplea argThat
	 *  argThat(s -> s.equals("1")), anyDouble como firma de invocacion en el stub para pay
	 * 	de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
	 * 	y en la segunda invocacion de pay (para toBePaid) no haga nada. El metodo pay acepta cualquier argumento
	 * 	indicado mediante ArgumentMatcher any.
	 * 	Comprueba que al invocar employeeManager.payEmployees se paga a solo 1 Employee.
	 *  A continuacion, verifica las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
	 *  para el que se lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion que si recibe el pago
	 *  chequeando la interaccion con el metodo setPaid a false y true respectivamente.
	 */
	@Test
	public void testArgumentMatcherExample() {
		// Crea una lista con dos empleados diferentes
		List<Employee> employees = Arrays.asList(notToBePaid, toBePaid);

		// Crea un stub when-thenReturn para employeeRepository.findAll que devuelva la lista con los empleados
		when(employeeRepository.findAll()).thenReturn(employees);

		// Crea un stub con encadenamiento para 2 llamadas doThrow-when en bankService.pay
		// La primera invocación lanza una RuntimeException para notToBePaid
		// La segunda invocación no hace nada para toBePaid
		doThrow(new RuntimeException()).doNothing().when(bankService).pay(argThat(s -> s.equals("1")), anyDouble());

		//Creamos un Assert
		assertThat(employeeManager.payEmployees()).isEqualTo(1);

		// Verifica que notToBePaid no se haya pagado y que toBePaid sí
		verify(notToBePaid).setPaid(false);
		verify(toBePaid).setPaid(true);
	}
}
