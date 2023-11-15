package com.application.labgui.Repository;



import com.application.labgui.AppExceptions.ValidationException;
import com.application.labgui.Domain.Entitate;

import java.util.Optional;

/**
 * CRUD operation repository interface
 * @param <ID> - type E must have an attrivute of type ID
 * @param <E> - type of entities saved in repository
 */
public interface Repository<ID, E extends Entitate<ID>> {

    /**
     * functie de gasire a unei entitati
     * @param id the id of the entity to be returned
     *           id most not be null
     * @return an {@code Optional} encapsulating the entity with the given id
     */
    Optional<E> findOne(ID id);

    /**
     *
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     *
     * @param entity
     *         entity must be not null
     *  @return an {@code Optional} - null if the entity was saved,
     *                               the entity (id already exists
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.     *
     */
    Optional<E> save(E entity);

    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @return an {@code Optional}
     *          - null if there is no entity with the given id,
     *          - the removed entity, otherwise
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    Optional<E> delete(ID id);

    /**
     *
     * @param entity
     *          entity must not be null
     * @return an {@code Optional}
     * - null if the entity was updated
     * - otherwise (e.g. id does not exist) returns the entity.
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
    Optional<E> update(E entity);

    /**
     *
     * @return dimensiunea repositoryului
     */
    int size();

}
