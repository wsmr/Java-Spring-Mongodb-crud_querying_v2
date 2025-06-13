package com.diyawanna.sup.service;

import com.diyawanna.sup.entity.Faculty;
import com.diyawanna.sup.repository.FacultyRepository;
import com.diyawanna.sup.exception.FacultyNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Faculty service for business logic and CRUD operations
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    @Cacheable(value = "faculties", key = "'all_active'")
    public List<Faculty> getAllActiveFaculties() {
        return facultyRepository.findByActiveTrue();
    }

    @Cacheable(value = "faculties", key = "#id")
    public Faculty getFacultyById(String id) {
        Optional<Faculty> faculty = facultyRepository.findById(id);
        if (faculty.isEmpty()) {
            throw new FacultyNotFoundException("Faculty not found with id: " + id);
        }
        return faculty.get();
    }

    @CacheEvict(value = "faculties", allEntries = true)
    public Faculty createFaculty(Faculty faculty) {
        faculty.setActive(true);
        faculty.setCreatedAt(LocalDateTime.now());
        faculty.setUpdatedAt(LocalDateTime.now());
        return facultyRepository.save(faculty);
    }

    @CachePut(value = "faculties", key = "#id")
    public Faculty updateFaculty(String id, Faculty facultyUpdate) {
        Faculty existingFaculty = getFacultyById(id);

        if (facultyUpdate.getName() != null) {
            existingFaculty.setName(facultyUpdate.getName());
        }
        if (facultyUpdate.getDescription() != null) {
            existingFaculty.setDescription(facultyUpdate.getDescription());
        }
        if (facultyUpdate.getUniversityId() != null) {
            existingFaculty.setUniversityId(facultyUpdate.getUniversityId());
        }
        if (facultyUpdate.getUniversityName() != null) {
            existingFaculty.setUniversityName(facultyUpdate.getUniversityName());
        }
        if (facultyUpdate.getDean() != null) {
            existingFaculty.setDean(facultyUpdate.getDean());
        }
        if (facultyUpdate.getContactEmail() != null) {
            existingFaculty.setContactEmail(facultyUpdate.getContactEmail());
        }
        if (facultyUpdate.getContactPhone() != null) {
            existingFaculty.setContactPhone(facultyUpdate.getContactPhone());
        }
        if (facultyUpdate.getSubjects() != null) {
            existingFaculty.setSubjects(facultyUpdate.getSubjects());
        }

        existingFaculty.setUpdatedAt(LocalDateTime.now());
        return facultyRepository.save(existingFaculty);
    }

    @CacheEvict(value = "faculties", key = "#id")
    public void deleteFaculty(String id) {
        Faculty faculty = getFacultyById(id);
        faculty.setActive(false);
        faculty.setUpdatedAt(LocalDateTime.now());
        facultyRepository.save(faculty);
    }

    public List<Faculty> getFacultiesByUniversity(String universityId) {
        return facultyRepository.findByUniversityIdAndActiveTrue(universityId);
    }

    public List<Faculty> searchFacultiesByName(String name) {
        return facultyRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Faculty> getFacultiesWithSubject(String subject) {
        return facultyRepository.findBySubjectsContainingAndActiveTrue(subject);
    }

    @CachePut(value = "faculties", key = "#facultyId")
    public Faculty addSubjectToFaculty(String facultyId, String subject) {
        Faculty faculty = getFacultyById(facultyId);
        faculty.addSubject(subject);
        faculty.setUpdatedAt(LocalDateTime.now());
        return facultyRepository.save(faculty);
    }

    @CachePut(value = "faculties", key = "#facultyId")
    public Faculty removeSubjectFromFaculty(String facultyId, String subject) {
        Faculty faculty = getFacultyById(facultyId);
        faculty.removeSubject(subject);
        faculty.setUpdatedAt(LocalDateTime.now());
        return facultyRepository.save(faculty);
    }

    public long countActiveFaculties() {
        return facultyRepository.countByActiveTrue();
    }

    public long countFacultiesByUniversity(String universityId) {
        return facultyRepository.countByUniversityIdAndActiveTrue(universityId);
    }
}

