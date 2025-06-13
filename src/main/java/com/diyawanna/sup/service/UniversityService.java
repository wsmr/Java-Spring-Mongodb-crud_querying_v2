package com.diyawanna.sup.service;

import com.diyawanna.sup.entity.University;
import com.diyawanna.sup.repository.UniversityRepository;
import com.diyawanna.sup.exception.UniversityNotFoundException;
import com.diyawanna.sup.exception.UniversityAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * University service for business logic and CRUD operations
 * 
 * This service provides:
 * - Complete CRUD operations for universities
 * - Business logic validation
 * - Caching for performance optimization
 * - Search and filtering capabilities
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class UniversityService {

    @Autowired
    private UniversityRepository universityRepository;

    /**
     * Get all active universities
     */
    @Cacheable(value = "universities", key = "'all_active'")
    public List<University> getAllActiveUniversities() {
        return universityRepository.findByActiveTrue();
    }

    /**
     * Get university by ID
     */
    @Cacheable(value = "universities", key = "#id")
    public University getUniversityById(String id) {
        Optional<University> university = universityRepository.findById(id);
        if (university.isEmpty()) {
            throw new UniversityNotFoundException("University not found with id: " + id);
        }
        return university.get();
    }

    /**
     * Get university by name
     */
    @Cacheable(value = "universities", key = "'name_' + #name")
    public University getUniversityByName(String name) {
        Optional<University> university = universityRepository.findByName(name);
        if (university.isEmpty()) {
            throw new UniversityNotFoundException("University not found with name: " + name);
        }
        return university.get();
    }

    /**
     * Create new university
     */
    @CacheEvict(value = "universities", allEntries = true)
    public University createUniversity(University university) {
        // Validate name uniqueness
        if (universityRepository.existsByName(university.getName())) {
            throw new UniversityAlreadyExistsException("University already exists with name: " + university.getName());
        }

        // Set default values
        university.setActive(true);
        university.setCreatedAt(LocalDateTime.now());
        university.setUpdatedAt(LocalDateTime.now());

        return universityRepository.save(university);
    }

    /**
     * Update university
     */
    @CachePut(value = "universities", key = "#id")
    @CacheEvict(value = "universities", key = "'all_active'")
    public University updateUniversity(String id, University universityUpdate) {
        University existingUniversity = getUniversityById(id);

        // Update fields if provided
        if (universityUpdate.getName() != null) {
            // Check name uniqueness
            if (!universityUpdate.getName().equals(existingUniversity.getName()) && 
                universityRepository.existsByName(universityUpdate.getName())) {
                throw new UniversityAlreadyExistsException("University already exists with name: " + universityUpdate.getName());
            }
            existingUniversity.setName(universityUpdate.getName());
        }
        if (universityUpdate.getDescription() != null) {
            existingUniversity.setDescription(universityUpdate.getDescription());
        }
        if (universityUpdate.getLocation() != null) {
            existingUniversity.setLocation(universityUpdate.getLocation());
        }
        if (universityUpdate.getWebsite() != null) {
            existingUniversity.setWebsite(universityUpdate.getWebsite());
        }
        if (universityUpdate.getContactEmail() != null) {
            existingUniversity.setContactEmail(universityUpdate.getContactEmail());
        }
        if (universityUpdate.getContactPhone() != null) {
            existingUniversity.setContactPhone(universityUpdate.getContactPhone());
        }
        if (universityUpdate.getFaculties() != null) {
            existingUniversity.setFaculties(universityUpdate.getFaculties());
        }

        existingUniversity.setUpdatedAt(LocalDateTime.now());
        return universityRepository.save(existingUniversity);
    }

    /**
     * Delete university (soft delete)
     */
    @CacheEvict(value = "universities", key = "#id")
    public void deleteUniversity(String id) {
        University university = getUniversityById(id);
        university.setActive(false);
        university.setUpdatedAt(LocalDateTime.now());
        universityRepository.save(university);
    }

    /**
     * Hard delete university
     */
    @CacheEvict(value = "universities", allEntries = true)
    public void hardDeleteUniversity(String id) {
        if (!universityRepository.existsById(id)) {
            throw new UniversityNotFoundException("University not found with id: " + id);
        }
        universityRepository.deleteById(id);
    }

    /**
     * Search universities by name
     */
    public List<University> searchUniversitiesByName(String name) {
        return universityRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Search universities by description
     */
    public List<University> searchUniversitiesByDescription(String description) {
        return universityRepository.findByDescriptionContainingIgnoreCase(description);
    }

    /**
     * Get universities by location
     */
    public List<University> getUniversitiesByLocation(String location) {
        return universityRepository.findByLocationContainingIgnoreCaseAndActiveTrue(location);
    }

    /**
     * Get universities that have specific faculty
     */
    public List<University> getUniversitiesWithFaculty(String facultyId) {
        return universityRepository.findByFacultiesContainingAndActiveTrue(facultyId);
    }

    /**
     * Get universities with website
     */
    public List<University> getUniversitiesWithWebsite() {
        return universityRepository.findUniversitiesWithWebsite();
    }

    /**
     * Add faculty to university
     */
    @CachePut(value = "universities", key = "#universityId")
    public University addFacultyToUniversity(String universityId, String facultyId) {
        University university = getUniversityById(universityId);
        university.addFaculty(facultyId);
        university.setUpdatedAt(LocalDateTime.now());
        return universityRepository.save(university);
    }

    /**
     * Remove faculty from university
     */
    @CachePut(value = "universities", key = "#universityId")
    public University removeFacultyFromUniversity(String universityId, String facultyId) {
        University university = getUniversityById(universityId);
        university.removeFaculty(facultyId);
        university.setUpdatedAt(LocalDateTime.now());
        return universityRepository.save(university);
    }

    /**
     * Count active universities
     */
    public long countActiveUniversities() {
        return universityRepository.countByActiveTrue();
    }

    /**
     * Count universities by location
     */
    public long countUniversitiesByLocation(String location) {
        return universityRepository.countByLocationContainingIgnoreCaseAndActiveTrue(location);
    }

    /**
     * Check if university name exists
     */
    public boolean universityNameExists(String name) {
        return universityRepository.existsByName(name);
    }

    /**
     * Activate university
     */
    @CachePut(value = "universities", key = "#id")
    public University activateUniversity(String id) {
        University university = getUniversityById(id);
        university.setActive(true);
        university.setUpdatedAt(LocalDateTime.now());
        return universityRepository.save(university);
    }

    /**
     * Deactivate university
     */
    @CachePut(value = "universities", key = "#id")
    public University deactivateUniversity(String id) {
        University university = getUniversityById(id);
        university.setActive(false);
        university.setUpdatedAt(LocalDateTime.now());
        return universityRepository.save(university);
    }
}

