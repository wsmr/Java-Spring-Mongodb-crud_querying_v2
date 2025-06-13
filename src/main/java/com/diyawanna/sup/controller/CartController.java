package com.diyawanna.sup.controller;

import com.diyawanna.sup.entity.Cart;
import com.diyawanna.sup.entity.Cart.CartItem;
import com.diyawanna.sup.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Cart controller for REST API endpoints
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<?> getAllCarts() {
        try {
            List<Cart> carts = cartService.getAllActiveCarts();
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve carts");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCartById(@PathVariable String id) {
        try {
            Cart cart = cartService.getCartById(id);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cart not found");
            error.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createCart(@Valid @RequestBody Cart cart) {
        try {
            Cart createdCart = cartService.createCart(cart);
            return ResponseEntity.ok(createdCart);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create cart");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCart(@PathVariable String id, @RequestBody Cart cart) {
        try {
            Cart updatedCart = cartService.updateCart(id, cart);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update cart");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable String id) {
        try {
            cartService.deleteCart(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete cart");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCartsByUser(@PathVariable String userId) {
        try {
            List<Cart> carts = cartService.getCartsByUser(userId);
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve carts");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getUserActiveCart(@PathVariable String userId) {
        try {
            Optional<Cart> cart = cartService.getUserActiveCart(userId);
            if (cart.isPresent()) {
                return ResponseEntity.ok(cart.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No active cart found for user");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve active cart");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getCartsByStatus(@PathVariable String status) {
        try {
            List<Cart> carts = cartService.getCartsByStatus(status);
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve carts");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItemToCart(@PathVariable String cartId, @RequestBody CartItem item) {
        try {
            Cart cart = cartService.addItemToCart(cartId, item);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add item to cart");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable String cartId, @PathVariable String itemId) {
        try {
            Cart cart = cartService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to remove item from cart");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{cartId}/status/{status}")
    public ResponseEntity<?> updateCartStatus(@PathVariable String cartId, @PathVariable String status) {
        try {
            Cart cart = cartService.updateCartStatus(cartId, status);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update cart status");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/search/name")
    public ResponseEntity<?> searchCartsByName(@RequestParam String name) {
        try {
            List<Cart> carts = cartService.searchCartsByName(name);
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/amount-range")
    public ResponseEntity<?> getCartsByAmountRange(@RequestParam Double minAmount, @RequestParam Double maxAmount) {
        try {
            List<Cart> carts = cartService.getCartsByTotalAmountRange(minAmount, maxAmount);
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve carts");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getCartStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActiveCarts", cartService.countActiveCarts());
            stats.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

