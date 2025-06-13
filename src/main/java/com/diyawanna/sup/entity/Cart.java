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
 * Cart entity representing cart collection in MongoDB
 * 
 * This entity stores cart information including:
 * - Basic information (name, description)
 * - Associated items
 * - User reference
 * - Audit fields (created/modified dates)
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Document(collection = "cart")
public class Cart {

    @Id
    private String id;

    @NotBlank(message = "Cart name is required")
    private String name;

    private String description;
    
    @Indexed
    private String userId;
    
    private String userName;
    
    private List<CartItem> items = new ArrayList<>();
    
    private Double totalAmount = 0.0;
    
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, CANCELLED
    
    private boolean active = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Default constructor
    public Cart() {}

    // Constructor for essential fields
    public Cart(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    // Inner class for cart items
    public static class CartItem {
        private String itemId;
        private String itemName;
        private String description;
        private Integer quantity;
        private Double price;
        private Double subtotal;
        private String category;

        // Default constructor
        public CartItem() {}

        // Constructor
        public CartItem(String itemId, String itemName, Integer quantity, Double price) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = quantity * price;
        }

        // Getters and Setters
        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
            updateSubtotal();
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
            updateSubtotal();
        }

        public Double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(Double subtotal) {
            this.subtotal = subtotal;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        private void updateSubtotal() {
            if (quantity != null && price != null) {
                this.subtotal = quantity * price;
            }
        }

        @Override
        public String toString() {
            return "CartItem{" +
                    "itemId='" + itemId + '\'' +
                    ", itemName='" + itemName + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    ", subtotal=" + subtotal +
                    ", category='" + category + '\'' +
                    '}';
        }
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        calculateTotalAmount();
    }

    public void addItem(CartItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        calculateTotalAmount();
    }

    public void removeItem(String itemId) {
        if (this.items != null) {
            this.items.removeIf(item -> item.getItemId().equals(itemId));
            calculateTotalAmount();
        }
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    // Helper method to calculate total amount
    private void calculateTotalAmount() {
        if (this.items != null) {
            this.totalAmount = this.items.stream()
                    .mapToDouble(item -> item.getSubtotal() != null ? item.getSubtotal() : 0.0)
                    .sum();
        } else {
            this.totalAmount = 0.0;
        }
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", items=" + items +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

