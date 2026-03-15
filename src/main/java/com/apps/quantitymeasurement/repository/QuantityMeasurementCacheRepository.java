package com.apps.quantitymeasurement.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.apps.quantitymeasurement.entity.QuantityMeasurementEntity;

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    static class AppendableObjectOutputStream extends ObjectOutputStream {


        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        /**
         * Writes the stream header only if the file
         * is empty or newly created.
         *
         * @throws IOException if writing fails
         */
        @Override
        protected void writeStreamHeader() throws IOException {
            File file = new File(QuantityMeasurementCacheRepository.FILE_NAME);

            if (!file.exists() || file.length() == 0) {
                super.writeStreamHeader();
            } else {
                reset();
            }
        }
    }

    /**
     * File location used for storing serialized repository data.
     */
    public static final String FILE_NAME = "data/quantity_measurement_repo.ser";

    /**
     * In-memory cache storing quantity measurement entities.
     */
    private List<QuantityMeasurementEntity> quantityMeasurementEntityCache;

    /**
     * Singleton repository instance.
     */
    private static QuantityMeasurementCacheRepository instance;

    /**
     * Private constructor to enforce Singleton pattern.
     *
     * Initializes the in-memory cache and loads previously
     * stored entities from disk.
     */
    private QuantityMeasurementCacheRepository() {
        quantityMeasurementEntityCache = new ArrayList<>();
        loadFromDisk();
    }

    /**
     * Returns the singleton instance of the repository.
     *
     * @return repository instance
     */
    public static QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            instance = new QuantityMeasurementCacheRepository();
        }
        return instance;
    }

    /**
     * Saves a {@link QuantityMeasurementEntity} into the repository.
     *
     * The entity is added to the in-memory cache and then
     * serialized to disk for persistence.
     *
     * @param entity quantity measurement entity to store
     */
    @Override
    public void save(QuantityMeasurementEntity entity) {

        quantityMeasurementEntityCache.add(entity);

        saveToDisk(entity);
    }

    /**
     * Returns all stored quantity measurement entities.
     *
     * @return list of cached entities
     */
    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {

        return quantityMeasurementEntityCache;
    }

    /**
     * Persists a quantity measurement entity to disk.
     *
     * Serialization is performed in append mode so that
     * previously stored objects remain intact.
     *
     * @param entity entity to persist
     */
    private void saveToDisk(QuantityMeasurementEntity entity) {

        try (
                FileOutputStream fos = new FileOutputStream(FILE_NAME, true);
                AppendableObjectOutputStream oos = new AppendableObjectOutputStream(fos)
        ) {
            oos.writeObject(entity);

        } catch (IOException e) {
            System.err.println("Error saving entity: " + e.getMessage());
        }
    }

    /**
     * Loads previously stored entities from disk into
     * the in-memory cache.
     *
     * This method is invoked during repository initialization
     * to restore previously saved measurement records.
     */
    private void loadFromDisk() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (
                FileInputStream fis = new FileInputStream(FILE_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {

            while (true) {

                try {
                    QuantityMeasurementEntity entity =
                            (QuantityMeasurementEntity) ois.readObject();

                    quantityMeasurementEntityCache.add(entity);

                } catch (EOFException e) {
                    break;
                }
            }

            System.out.println(
                    "Loaded " + quantityMeasurementEntityCache.size()
                            + " quantity measurement entities from storage"
            );

        } catch (IOException | ClassNotFoundException ex) {

            System.err.println(
                    "Error loading quantity measurement entities: "
                            + ex.getMessage()
            );
        }
    }

    /**
     * Main method for testing repository functionality.
     *
     * Demonstrates repository initialization, entity
     * persistence, cache retrieval, and serialization behavior.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        System.out.println("---- Testing QuantityMeasurementCacheRepository ----");

        QuantityMeasurementCacheRepository repo =
                QuantityMeasurementCacheRepository.getInstance();

        System.out.println("Initial cached entities: "
                + repo.getAllMeasurements().size());

        com.apps.quantitymeasurement.interfaces.IMeasurable feet =
                com.apps.quantitymeasurement.units.LengthUnit.FEET;

        com.apps.quantitymeasurement.interfaces.IMeasurable inches =
                com.apps.quantitymeasurement.units.LengthUnit.INCHES;

        com.apps.quantitymeasurement.model.QuantityModel<
                com.apps.quantitymeasurement.interfaces.IMeasurable> q1 =
                new com.apps.quantitymeasurement.model.QuantityModel<>(2, feet);

        com.apps.quantitymeasurement.model.QuantityModel<
                com.apps.quantitymeasurement.interfaces.IMeasurable> q2 =
                new com.apps.quantitymeasurement.model.QuantityModel<>(24, inches);

        com.apps.quantitymeasurement.model.QuantityModel<
                com.apps.quantitymeasurement.interfaces.IMeasurable> result =
                new com.apps.quantitymeasurement.model.QuantityModel<>(4, feet);

        com.apps.quantitymeasurement.entity.QuantityMeasurementEntity entity =
                new com.apps.quantitymeasurement.entity.QuantityMeasurementEntity(
                        q1,
                        q2,
                        "ADD",
                        result
                );

        repo.save(entity);

        System.out.println("Entity saved successfully.");

        repo.save(entity);

        System.out.println("Second entity saved (testing appendable stream).");

        java.util.List<
                com.apps.quantitymeasurement.entity.QuantityMeasurementEntity> list =
                repo.getAllMeasurements();

        System.out.println("\nTotal cached entities: " + list.size());

        System.out.println("\n---- Stored Entities ----");

        for (com.apps.quantitymeasurement.entity.QuantityMeasurementEntity e : list) {
            System.out.println(e);
        }

        System.out.println("\nRestart the program to verify loadFromDisk() works.");

        System.out.println("---- QuantityMeasurementCacheRepository Test Completed ----");
    }
}