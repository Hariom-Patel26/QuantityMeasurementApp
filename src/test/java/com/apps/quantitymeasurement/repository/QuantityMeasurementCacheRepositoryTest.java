package com.apps.quantitymeasurement.repository;

import com.apps.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.entity.QuantityModel;
import com.apps.quantitymeasurement.units.IMeasurable;
import com.apps.quantitymeasurement.units.LengthUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QuantityMeasurementCacheRepositoryTest
 *
 * Tests the cache repository layer:
 * - getInstance() always returns the same singleton instance
 * - save() adds entities to the in-memory cache
 * - getAllMeasurements() returns the full list
 * - getMeasurementsByOperation() filters by operation type
 * - getMeasurementsByType() filters by measurement category
 * - getTotalCount() returns the accurate count
 * - deleteAll() clears the cache and the .ser file
 * - Multiple saves accumulate correctly
 * - Implements IQuantityMeasurementRepository contract
 *
 * Each test starts with a clean repository (deleteAll in @BeforeEach) to prevent
 * interference from the persistent .ser file loaded at startup.
 */
public class QuantityMeasurementCacheRepositoryTest {

    private QuantityMeasurementCacheRepository repository;

    /* Shared fixtures */
    private QuantityModel<IMeasurable> q1;
    private QuantityModel<IMeasurable> q2;
    private QuantityModel<IMeasurable> resultModel;

    @BeforeEach
    public void setUp() {
        repository  = QuantityMeasurementCacheRepository.getInstance();
        /*
         * Clear all previously persisted data so each test starts
         * with a known-empty state. Also removes the .ser file from disk.
         */
        repository.deleteAll();
        q1          = new QuantityModel<>(2.0,  LengthUnit.FEET);
        q2          = new QuantityModel<>(24.0, LengthUnit.INCHES);
        resultModel = new QuantityModel<>(4.0,  LengthUnit.FEET);
    }

    // =========================================================================
    // SINGLETON
    // =========================================================================

    @Test
    public void testGetInstance_ReturnsSameInstance() {
        QuantityMeasurementCacheRepository a = QuantityMeasurementCacheRepository.getInstance();
        QuantityMeasurementCacheRepository b = QuantityMeasurementCacheRepository.getInstance();
        assertSame(a, b);
    }

    @Test
    public void testGetInstance_NotNull() {
        assertNotNull(QuantityMeasurementCacheRepository.getInstance());
    }

    // =========================================================================
    // IQuantityMeasurementRepository contract
    // =========================================================================

    @Test
    public void testImplementsRepositoryInterface() {
        assertTrue(repository instanceof IQuantityMeasurementRepository);
    }

    // =========================================================================
    // save() and getAllMeasurements()
    // =========================================================================

    @Test
    public void testSave_EntityAppearsInCache() {
        QuantityMeasurementEntity entity =
            new QuantityMeasurementEntity(q1, q2, "ADD", resultModel);
        repository.save(entity);

        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        assertEquals(1, all.size());
        assertTrue(all.contains(entity));
    }

    @Test
    public void testSave_MultipleEntities_AllAppearInCache() {
        QuantityMeasurementEntity e1 =
            new QuantityMeasurementEntity(q1, q2, "COMPARE", "Equal");
        QuantityMeasurementEntity e2 =
            new QuantityMeasurementEntity(q1, q2, "ADD", resultModel);

        repository.save(e1);
        repository.save(e2);

        assertEquals(2, repository.getAllMeasurements().size());
    }

    @Test
    public void testGetAllMeasurements_ReturnsNonNullList() {
        assertNotNull(repository.getAllMeasurements());
    }

    @Test
    public void testGetAllMeasurements_AfterSave_ReflectsNewState() {
        repository.save(new QuantityMeasurementEntity(q1, q2, "DIVIDE", "1.0"));
        assertEquals(1, repository.getAllMeasurements().size());
    }

    // =========================================================================
    // getTotalCount()
    // =========================================================================

    @Test
    public void testGetTotalCount_EmptyRepository_ReturnsZero() {
        assertEquals(0, repository.getTotalCount());
    }

    @Test
    public void testGetTotalCount_AfterSaves_ReturnsCorrectCount() {
        repository.save(new QuantityMeasurementEntity(q1, q2, "COMPARE", "Equal"));
        repository.save(new QuantityMeasurementEntity(q1, q2, "ADD", resultModel));
        assertEquals(2, repository.getTotalCount());
    }

    // =========================================================================
    // getMeasurementsByOperation()
    // =========================================================================

    @Test
    public void testGetMeasurementsByOperation_FiltersCorrectly() {
        repository.save(new QuantityMeasurementEntity(q1, q2, "COMPARE", "Equal"));
        repository.save(new QuantityMeasurementEntity(q1, q2, "ADD",     resultModel));

        List<QuantityMeasurementEntity> compareResults =
            repository.getMeasurementsByOperation("COMPARE");
        assertEquals(1, compareResults.size());
        assertEquals("COMPARE", compareResults.get(0).operation);
    }

    // =========================================================================
    // getMeasurementsByType()
    // =========================================================================

    @Test
    public void testGetMeasurementsByType_FiltersCorrectly() {
        repository.save(new QuantityMeasurementEntity(q1, q2, "COMPARE", "Equal"));

        List<QuantityMeasurementEntity> lengthResults =
            repository.getMeasurementsByType("LengthUnit");
        assertFalse(lengthResults.isEmpty());
        assertEquals("LengthUnit", lengthResults.get(0).thisMeasurementType);
    }

    // =========================================================================
    // deleteAll()
    // =========================================================================

    @Test
    public void testDeleteAll_ClearsCache() {
        repository.save(new QuantityMeasurementEntity(q1, q2, "COMPARE", "Equal"));
        repository.deleteAll();
        assertEquals(0, repository.getTotalCount());
        assertTrue(repository.getAllMeasurements().isEmpty());
    }
}