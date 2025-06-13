package com.diyawanna.sup.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * University entity representing university collection in MongoDB
 * 
 * This entity stores university information including:
 * - Basic information (name, description)
 * - Associated faculties
 * - Audit fields (created/modified dates)
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Document(collection = "university")
public class University {

    @Id
    private String id;

    @NotBlank(message = "University name is required")
    @Indexed(unique = true)
    private String name;

    private String description;
    
    private String location;
    
    private String website;
    
    private String contactEmail;
    
    private String contactPhone;
    
    private List<String> faculties = new ArrayList<>();
    
    private boolean active = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Default constructor
    public University() {}

    // Constructor for essential fields
    public University(String name) {
        this.name = name;
    }

    // Constructor with name and description
    public University(String name, String description) {
        this.name = name;
        this.description = description;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public List<String> getFaculties() {
        return faculties;
    }

    public void setFaculties(List<String> faculties) {
        this.faculties = faculties != null ? faculties : new ArrayList<>();
    }

    public void addFaculty(String facultyId) {
        if (this.faculties == null) {
            this.faculties = new ArrayList<>();
        }
        if (!this.faculties.contains(facultyId)) {
            this.faculties.add(facultyId);
        }
    }

    public void removeFaculty(String facultyId) {
        if (this.faculties != null) {
            this.faculties.remove(facultyId);
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
        return "University{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", faculties=" + faculties +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

