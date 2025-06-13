package com.diyawanna.sup.service;

import com.diyawanna.sup.entity.Query;
import com.diyawanna.sup.repository.QueryRepository;
import com.diyawanna.sup.exception.QueryNotFoundException;
import com.diyawanna.sup.exception.QueryAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Query service for business logic and CRUD operations
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class QueryService {

    @Autowired
    private QueryRepository queryRepository;

    @Cacheable(value = "queries", key = "'all_active'")
    public List<Query> getAllActiveQueries() {
        return queryRepository.findByActiveTrue();
    }

    @Cacheable(value = "queries", key = "#id")
    public Query getQueryById(String id) {
        Optional<Query> query = queryRepository.findById(id);
        if (query.isEmpty()) {
            throw new QueryNotFoundException("Query not found with id: " + id);
        }
        return query.get();
    }

    @Cacheable(value = "queries", key = "'name_' + #name")
    public Query getQueryByName(String name) {
        Optional<Query> query = queryRepository.findByName(name);
        if (query.isEmpty()) {
            throw new QueryNotFoundException("Query not found with name: " + name);
        }
        return query.get();
    }

    @CacheEvict(value = "queries", allEntries = true)
    public Query createQuery(Query query) {
        if (queryRepository.existsByName(query.getName())) {
            throw new QueryAlreadyExistsException("Query already exists with name: " + query.getName());
        }

        query.setActive(true);
        query.setCreatedAt(LocalDateTime.now());
        query.setUpdatedAt(LocalDateTime.now());
        return queryRepository.save(query);
    }

    @CachePut(value = "queries", key = "#id")
    public Query updateQuery(String id, Query queryUpdate) {
        Query existingQuery = getQueryById(id);

        if (queryUpdate.getName() != null) {
            if (!queryUpdate.getName().equals(existingQuery.getName()) && 
                queryRepository.existsByName(queryUpdate.getName())) {
                throw new QueryAlreadyExistsException("Query already exists with name: " + queryUpdate.getName());
            }
            existingQuery.setName(queryUpdate.getName());
        }
        if (queryUpdate.getDescription() != null) {
            existingQuery.setDescription(queryUpdate.getDescription());
        }
        if (queryUpdate.getQueryContent() != null) {
            existingQuery.setQueryContent(queryUpdate.getQueryContent());
        }
        if (queryUpdate.getQueryType() != null) {
            existingQuery.setQueryType(queryUpdate.getQueryType());
        }
        if (queryUpdate.getCollection() != null) {
            existingQuery.setCollection(queryUpdate.getCollection());
        }
        if (queryUpdate.getParameters() != null) {
            existingQuery.setParameters(queryUpdate.getParameters());
        }
        if (queryUpdate.getVariableMappings() != null) {
            existingQuery.setVariableMappings(queryUpdate.getVariableMappings());
        }
        if (queryUpdate.getSuccessMessage() != null) {
            existingQuery.setSuccessMessage(queryUpdate.getSuccessMessage());
        }
        if (queryUpdate.getErrorMessage() != null) {
            existingQuery.setErrorMessage(queryUpdate.getErrorMessage());
        }
        if (queryUpdate.getCategory() != null) {
            existingQuery.setCategory(queryUpdate.getCategory());
        }
        if (queryUpdate.isCacheable() != existingQuery.isCacheable()) {
            existingQuery.setCacheable(queryUpdate.isCacheable());
        }
        if (queryUpdate.getCacheTimeoutSeconds() != null) {
            existingQuery.setCacheTimeoutSeconds(queryUpdate.getCacheTimeoutSeconds());
        }
        if (queryUpdate.getLastModifiedBy() != null) {
            existingQuery.setLastModifiedBy(queryUpdate.getLastModifiedBy());
        }

        existingQuery.setUpdatedAt(LocalDateTime.now());
        return queryRepository.save(existingQuery);
    }

    @CacheEvict(value = "queries", key = "#id")
    public void deleteQuery(String id) {
        Query query = getQueryById(id);
        query.setActive(false);
        query.setUpdatedAt(LocalDateTime.now());
        queryRepository.save(query);
    }

    public List<Query> getQueriesByCategory(String category) {
        return queryRepository.findByCategoryAndActiveTrue(category);
    }

    public List<Query> getQueriesByType(String queryType) {
        return queryRepository.findByQueryTypeAndActiveTrue(queryType);
    }

    public List<Query> getQueriesByCollection(String collection) {
        return queryRepository.findByCollectionAndActiveTrue(collection);
    }

    public List<Query> getCacheableQueries() {
        return queryRepository.findByCacheableAndActiveTrue(true);
    }

    public List<Query> searchQueriesByName(String name) {
        return queryRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Query> searchQueriesByDescription(String description) {
        return queryRepository.findByDescriptionContainingIgnoreCase(description);
    }

    public List<Query> getQueriesByCreator(String createdBy) {
        return queryRepository.findByCreatedByAndActiveTrue(createdBy);
    }

    public long countActiveQueries() {
        return queryRepository.countByActiveTrue();
    }

    public long countQueriesByCategory(String category) {
        return queryRepository.countByCategoryAndActiveTrue(category);
    }

    public long countQueriesByType(String queryType) {
        return queryRepository.countByQueryTypeAndActiveTrue(queryType);
    }

    public long countCacheableQueries() {
        return queryRepository.countByCacheableAndActiveTrue(true);
    }

    public boolean queryNameExists(String name) {
        return queryRepository.existsByName(name);
    }

    @CachePut(value = "queries", key = "#id")
    public Query activateQuery(String id) {
        Query query = getQueryById(id);
        query.setActive(true);
        query.setUpdatedAt(LocalDateTime.now());
        return queryRepository.save(query);
    }

    @CachePut(value = "queries", key = "#id")
    public Query deactivateQuery(String id) {
        Query query = getQueryById(id);
        query.setActive(false);
        query.setUpdatedAt(LocalDateTime.now());
        return queryRepository.save(query);
    }
}

