package com.app.quantitymeasurement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.app.quantitymeasurement.dto.request.QuantityMeasurementDTO;
import com.app.quantitymeasurement.dto.response.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.entity.User;
import com.app.quantitymeasurement.enums.AuthProvider;
import com.app.quantitymeasurement.enums.Role;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuantityMeasurementServiceTest {

	@Mock
	private QuantityMeasurementRepository repository;

	@InjectMocks
	private QuantityMeasurementServiceImpl service;

	private QuantityDTO feetDTO;
	private QuantityDTO inchesDTO;
	private QuantityDTO kilogramDTO;
	private User testUser;

	@BeforeEach
	void setUp() {
		feetDTO = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
		inchesDTO = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
		kilogramDTO = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
		testUser = User.builder().id(1L).email("test@example.com").provider(AuthProvider.LOCAL).role(Role.USER).build();
		when(repository.save(any(QuantityMeasurementEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
	}

	// COMPARE
	@Test
	void testCompare_EqualQuantities_ResultStringTrue() {
		QuantityMeasurementDTO result = service.compare(feetDTO, inchesDTO, testUser);
		assertNotNull(result);
		assertEquals("true", result.getResultString());
		assertEquals("compare", result.getOperation());
		assertFalse(result.isError());
		verify(repository, times(1)).save(any());
	}

	@Test
	void testCompare_NotEqual_ResultStringFalse() {
		QuantityDTO twoFeet = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
		QuantityMeasurementDTO result = service.compare(twoFeet, inchesDTO, testUser);
		assertEquals("false", result.getResultString());
	}

	@Test
	void testCompare_DifferentCategories_ThrowsAndSavesError() {
		assertThrows(QuantityMeasurementException.class, () -> service.compare(feetDTO, kilogramDTO, testUser));
		verify(repository, atLeastOnce()).save(any());
	}

	@Test
	void testCompare_NullUser_StillWorks() {
		QuantityMeasurementDTO result = service.compare(feetDTO, inchesDTO, null);
		assertNotNull(result);
		assertEquals("true", result.getResultString());
	}

	// CONVERT
	@Test
	void testConvert_FeetToInches_Returns12() {
		QuantityDTO targetInches = new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCHES);
		QuantityMeasurementDTO result = service.convert(feetDTO, targetInches, testUser);
		assertEquals(12.0, result.getResultValue(), 1e-4);
		assertEquals("convert", result.getOperation());
	}

	@Test
	void testConvert_CelsiusToFahrenheit_Returns32() {
		QuantityDTO celsius = new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS);
		QuantityDTO fahrenheit = new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.FAHRENHEIT);
		QuantityMeasurementDTO result = service.convert(celsius, fahrenheit, testUser);
		assertEquals(32.0, result.getResultValue(), 1e-4);
	}

	// ADD
	@Test
	void testAdd_FeetPlusInches_ResultInFeet() {
		QuantityMeasurementDTO result = service.add(feetDTO, inchesDTO, testUser);
		assertEquals(2.0, result.getResultValue(), 1e-4);
		assertEquals("FEET", result.getResultUnit());
		assertEquals("add", result.getOperation());
	}

	@Test
	void testAdd_WithTargetUnit_ResultInYards() {
		QuantityDTO yardsTarget = new QuantityDTO(0.0, QuantityDTO.LengthUnit.YARDS);
		QuantityMeasurementDTO result = service.add(feetDTO, inchesDTO, yardsTarget, testUser);
		assertEquals("YARDS", result.getResultUnit());
		assertTrue(result.getResultValue() > 0.0);
	}

	@Test
	void testAdd_TemperatureUnits_ThrowsUnsupported() {
		QuantityDTO celsius = new QuantityDTO(10.0, QuantityDTO.TemperatureUnit.CELSIUS);
		QuantityDTO fahrenheit = new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.FAHRENHEIT);
		assertThrows(QuantityMeasurementException.class, () -> service.add(celsius, fahrenheit, testUser));
	}

	@Test
	void testAdd_DifferentCategories_ThrowsAndSavesError() {
		assertThrows(QuantityMeasurementException.class, () -> service.add(feetDTO, kilogramDTO, testUser));
		verify(repository, atLeastOnce()).save(any());
	}

	// SUBTRACT
	@Test
	void testSubtract_FeetMinusInches_ResultZeroFeet() {
		QuantityMeasurementDTO result = service.subtract(feetDTO, inchesDTO, testUser);
		assertEquals(0.0, result.getResultValue(), 1e-4);
		assertEquals("FEET", result.getResultUnit());
	}

	@Test
	void testSubtract_WithTargetUnit_ResultInYards() {
		QuantityDTO yardsTarget = new QuantityDTO(0.0, QuantityDTO.LengthUnit.YARDS);
		QuantityMeasurementDTO result = service.subtract(feetDTO, inchesDTO, yardsTarget, testUser);
		assertEquals("YARDS", result.getResultUnit());
	}

	// DIVIDE
	@Test
	void testDivide_FeetByFeet_ResultOne() {
		QuantityMeasurementDTO result = service.divide(feetDTO, feetDTO, testUser);
		assertEquals(1.0, result.getResultValue(), 1e-4);
		assertEquals("divide", result.getOperation());
	}

	@Test
	void testDivide_ByZero_ThrowsArithmeticException() {
		QuantityDTO zeroInches = new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCHES);
		assertThrows(ArithmeticException.class, () -> service.divide(feetDTO, zeroInches, testUser));
	}

	// History / Count (user-scoped)
	@Test
	void testGetHistoryByOperation_DelegatesToRepository() {
		QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
		entity.setOperation("compare");
		entity.setResultString("true");

		when(repository.findByUserAndOperation(testUser, "compare")).thenReturn(List.of(entity));

		List<QuantityMeasurementDTO> result = service.getHistoryByOperation("compare", testUser);
		assertEquals(1, result.size());
		assertEquals("compare", result.get(0).getOperation());
		verify(repository, times(1)).findByUserAndOperation(testUser, "compare");
	}

	@Test
	void testGetHistoryByMeasurementType_DelegatesToRepository() {
		QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
		entity.setThisMeasurementType("LengthUnit");

		when(repository.findByUserAndThisMeasurementType(testUser, "LengthUnit")).thenReturn(List.of(entity));

		List<QuantityMeasurementDTO> result = service.getHistoryByMeasurementType("LengthUnit", testUser);
		assertEquals(1, result.size());
		verify(repository, times(1)).findByUserAndThisMeasurementType(testUser, "LengthUnit");
	}

	@Test
	void testGetOperationCount_DelegatesToRepository() {
		when(repository.countByUserAndOperationAndErrorFalse(testUser, "compare")).thenReturn(3L);
		long count = service.getOperationCount("compare", testUser);
		assertEquals(3L, count);
	}

	@Test
	void testGetErrorHistory_DelegatesToRepository() {
		QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity();
		errorEntity.setError(true);
		errorEntity.setErrorMessage("Test error");

		when(repository.findByErrorTrue()).thenReturn(List.of(errorEntity));

		List<QuantityMeasurementDTO> result = service.getErrorHistory();
		assertEquals(1, result.size());
		assertTrue(result.get(0).isError());
	}
}