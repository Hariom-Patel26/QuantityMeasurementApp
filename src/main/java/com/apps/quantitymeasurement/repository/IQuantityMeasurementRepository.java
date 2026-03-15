package com.apps.quantitymeasurement.repository;

import java.util.List;

import com.apps.quantitymeasurement.entity.QuantityMeasurementEntity;

/**
 * IQuantityMeasurementRepository
 *
 * Repository interface for managing {@link QuantityMeasurementEntity} objects.
 *
 * This interface defines the contract for data access operations related to
 * quantity measurement entities. It abstracts the persistence mechanism so
 * that the service layer does not depend on implementation details of storage.
 */
public interface IQuantityMeasurementRepository {

    /**
     * Saves a {@link QuantityMeasurementEntity} to the repository.
     *
     * @param entity the quantity measurement entity to be stored
     */
    void save(QuantityMeasurementEntity entity);

    /**
     * Retrieves all stored quantity measurement entities.
     *
     * @return list of all stored {@link QuantityMeasurementEntity} objects
     */
    List<QuantityMeasurementEntity> getAllMeasurements();

    /**
     * Main method used for simple testing of the repository interface.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Testing IQuantityMeasurementRepository interface");
    }
}