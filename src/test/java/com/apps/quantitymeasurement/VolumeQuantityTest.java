package com.apps.quantitymeasurement; 

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VolumeQuantityTest {
	private static final double EPSILON = 1e-3;

	// Volume Equality Comparisons 
	@Test
	public void testEquality_LitreToLitre_SameValue() {
		Quantity<VolumeUnit> litre1 = new Quantity<>(1.0, VolumeUnit.LITRE);
		Quantity<VolumeUnit> litre2 = new Quantity<>(1.0, VolumeUnit.LITRE);
		assertTrue(litre1.equals(litre2));
	}

	@Test
	public void testEquality_LitreToMillilitre_EquivalentValue() {
		// 1 Litre = 1000 Millilitres
		Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
		Quantity<VolumeUnit> ml = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
		assertTrue(litre.equals(ml));
	}

	@Test
	public void testEquality_GallonToLitre_EquivalentValue() {
		// 1 Gallon = 3.78541 Litres
		Quantity<VolumeUnit> gallon = new Quantity<>(1.0, VolumeUnit.GALLON);
		Quantity<VolumeUnit> litre = new Quantity<>(3.78541, VolumeUnit.LITRE);
		assertTrue(gallon.equals(litre));
	}

	// Volume Unit Conversions 
	@Test
	public void testConversion_LitreToMillilitre() {
		Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
		Quantity<VolumeUnit> result = litre.convertTo(VolumeUnit.MILLILITRE);
		assertEquals(1000.0, result.getValue(), EPSILON);
	}

	@Test
	public void testConversion_GallonToLitre() {
		Quantity<VolumeUnit> gallon = new Quantity<>(1.0, VolumeUnit.GALLON);
		Quantity<VolumeUnit> result = gallon.convertTo(VolumeUnit.LITRE);
		assertEquals(3.78541, result.getValue(), EPSILON);
	}

	// Volume Addition Operations
	@Test
	public void testAddition_LitrePlusMillilitre_InLitre() {
		// 1L + 1000mL = 2L
		Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
		Quantity<VolumeUnit> ml = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
		Quantity<VolumeUnit> result = litre.add(ml, VolumeUnit.LITRE);
		assertEquals(2.0, result.getValue(), EPSILON);
	}

	@Test
	public void testAddition_GallonPlusLitre_InGallon() {
		// 1 Gal + 3.78541 L = 2 Gal
		Quantity<VolumeUnit> gallon = new Quantity<>(1.0, VolumeUnit.GALLON);
		Quantity<VolumeUnit> litre = new Quantity<>(3.78541, VolumeUnit.LITRE);
		Quantity<VolumeUnit> result = gallon.add(litre, VolumeUnit.GALLON);
		assertEquals(2.0, result.getValue(), EPSILON);
	}

	//Cross-Category Prevention (Strict Isolation)  
	@Test
	public void testEquality_VolumeVsLength_Incompatible() {
		// Verifies that 1 Litre != 1 Foot
		Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
		Quantity<LengthUnit> foot = new Quantity<>(1.0, LengthUnit.FEET);
		assertFalse(litre.equals(foot));
	}

	@Test
	public void testEquality_VolumeVsWeight_Incompatible() {
		// Verifies that 1 Litre != 1 Kilogram
		Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
		Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
		assertFalse(litre.equals(kg));
	}

	//   Standalone Enum Verification ---
	@Test
	public void testVolumeUnitEnum_FactorAccuracy() {
		assertEquals(1.0, VolumeUnit.LITRE.getConversionFactor(), EPSILON);
		assertEquals(0.001, VolumeUnit.MILLILITRE.getConversionFactor(), EPSILON);
		assertEquals(3.78541, VolumeUnit.GALLON.getConversionFactor(), EPSILON);
	}
}