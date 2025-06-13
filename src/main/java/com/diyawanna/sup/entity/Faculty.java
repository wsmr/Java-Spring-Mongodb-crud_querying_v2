package com.diyawanna.sup.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Faculty entity representing faculty collection in MongoDB
 * 
 * This entity stores faculty information including:
 * - Basic information (name, description)
 * - Associated subjects
 * - University reference
 * - Audit fields (created/modified dates)
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Document(collection = "faculty")
public class Faculty {

    @Id
    private String id;

    @NotBlank(message = "Faculty name is required")
    @Indexed
    private String name;

    private String description;
    
    private String universityId;
    
    private String universityName;
    
    private String dean;
    
    private String contactEmail;
    
    private String contactPhone;
    
    private List<String> subjects = new ArrayList<>();
    
    private boolean active = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Default constructor
    public Faculty() {}

    // Constructor for essential fields
    public Faculty(String name) {
        this.name = name;
    }

    // Constructor with name and university
    public Faculty(String name, String universityId) {
        this.name = name;
        this.universityId = universityId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getDean() {
        return dean;
    }

    public void setDean(String dean) {
        this.dean = dean;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects != null ? subjects : new ArrayList<>();
    }

    public void addSubject(String subject) {
        if (this.subjects == null) {
            this.subjects = new ArrayList<>();
        }
        if (!this.subjects.contains(subject)) {
            this.subjects.add(subject);
        }
    }

    public void removeSubject(String subject) {
        if (this.subjects != null) {
            this.subjects.remove(subject);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", universityId='" + universityId + '\'' +
                ", universityName='" + universityName + '\'' +
                ", dean='" + dean + '\'' +
                ", subjects=" + subjects +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

