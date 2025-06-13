package com.diyawanna.sup.service;

import com.diyawanna.sup.entity.Cart;
import com.diyawanna.sup.entity.Cart.CartItem;
import com.diyawanna.sup.repository.CartRepository;
import com.diyawanna.sup.exception.CartNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Cart service for business logic and CRUD operations
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Cacheable(value = "carts", key = "'all_active'")
    public List<Cart> getAllActiveCarts() {
        return cartRepository.findByActiveTrue();
    }

    @Cacheable(value = "carts", key = "#id")
    public Cart getCartById(String id) {
        Optional<Cart> cart = cartRepository.findById(id);
        if (cart.isEmpty()) {
            throw new CartNotFoundException("Cart not found with id: " + id);
        }
        return cart.get();
    }

    @CacheEvict(value = "carts", allEntries = true)
    public Cart createCart(Cart cart) {
        cart.setActive(true);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @CachePut(value = "carts", key = "#id")
    public Cart updateCart(String id, Cart cartUpdate) {
        Cart existingCart = getCartById(id);

        if (cartUpdate.getName() != null) {
            existingCart.setName(cartUpdate.getName());
        }
        if (cartUpdate.getDescription() != null) {
            existingCart.setDescription(cartUpdate.getDescription());
        }
        if (cartUpdate.getUserId() != null) {
            existingCart.setUserId(cartUpdate.getUserId());
        }
        if (cartUpdate.getUserName() != null) {
            existingCart.setUserName(cartUpdate.getUserName());
        }
        if (cartUpdate.getItems() != null) {
            existingCart.setItems(cartUpdate.getItems());
        }
        if (cartUpdate.getStatus() != null) {
            existingCart.setStatus(cartUpdate.getStatus());
        }

        existingCart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(existingCart);
    }

    @CacheEvict(value = "carts", key = "#id")
    public void deleteCart(String id) {
        Cart cart = getCartById(id);
        cart.setActive(false);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public List<Cart> getCartsByUser(String userId) {
        return cartRepository.findByUserIdAndActiveTrue(userId);
    }

    public List<Cart> getCartsByUserAndStatus(String userId, String status) {
        return cartRepository.findByUserIdAndStatusAndActiveTrue(userId, status);
    }

    public List<Cart> getCartsByStatus(String status) {
        return cartRepository.findByStatusAndActiveTrue(status);
    }

    @CachePut(value = "carts", key = "#cartId")
    public Cart addItemToCart(String cartId, CartItem item) {
        Cart cart = getCartById(cartId);
        cart.addItem(item);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @CachePut(value = "carts", key = "#cartId")
    public Cart removeItemFromCart(String cartId, String itemId) {
        Cart cart = getCartById(cartId);
        cart.removeItem(itemId);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @CachePut(value = "carts", key = "#cartId")
    public Cart updateCartStatus(String cartId, String status) {
        Cart cart = getCartById(cartId);
        cart.setStatus(status);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public List<Cart> searchCartsByName(String name) {
        return cartRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Cart> getCartsContainingItem(String itemId) {
        return cartRepository.findCartsContainingItem(itemId);
    }

    public List<Cart> getCartsByTotalAmountRange(Double minAmount, Double maxAmount) {
        return cartRepository.findByTotalAmountBetweenAndActiveTrue(minAmount, maxAmount);
    }

    public long countActiveCarts() {
        return cartRepository.countByActiveTrue();
    }

    public long countCartsByUser(String userId) {
        return cartRepository.countByUserIdAndActiveTrue(userId);
    }

    public long countCartsByStatus(String status) {
        return cartRepository.countByStatusAndActiveTrue(status);
    }

    public Optional<Cart> getUserActiveCart(String userId) {
        return cartRepository.findByUserIdAndStatusAndActiveTrue(userId, "ACTIVE");
    }
}

