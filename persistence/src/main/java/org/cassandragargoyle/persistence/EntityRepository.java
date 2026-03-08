/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.persistence;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for entity persistence operations
 *
 * @param <T> the entity type
 * @param <ID> the identifier type
 * @author Zdenek
 * @since 2026-03-01
 */
public interface EntityRepository<T, ID>
{
	Optional<T> findById(ID id);

	List<T> findAll();

	T save(T entity);

	void delete(T entity);

	void deleteById(ID id);

	boolean existsById(ID id);

	long count();
}
