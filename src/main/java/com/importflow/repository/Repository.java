package com.importflow.repository;

import java.util.List;

/**
 * Abstraction for persisting successfully processed records.
 *
 * <p>The framework never knows about MySQL, PostgreSQL, MongoDB,
 * or any specific storage technology. It only knows this interface.
 * Plugins provide concrete implementations.</p>
 *
 * @param <T> the domain type to persist
 */
public interface Repository<T> {

    /**
     * Save a single record.
     *
     * @param record the domain object to persist
     */
    void save(T record);

    /**
     * Save a batch of records.
     * Default implementation loops over {@link #save(Object)}.
     *
     * @param records the list of domain objects to persist
     */
    default void saveAll(List<T> records) {
        for (T record : records) {
            save(record);
        }
    }

    /**
     * Retrieve all persisted records (useful for verification).
     *
     * @return all stored records
     */
    List<T> findAll();
}
