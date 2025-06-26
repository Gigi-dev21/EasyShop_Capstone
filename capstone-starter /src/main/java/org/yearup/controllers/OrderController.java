package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.*;
import org.yearup.models.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class OrderController {
    private final ShoppingCartDao shoppingCartDao;
    private final ProfileDao profileDao;
    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;
    private final UserDao userDao;

    @Autowired
    public OrderController(
            ShoppingCartDao shoppingCartDao,
            ProfileDao profileDao,
            OrderDao orderDao,
            OrderLineItemDao orderLineItemDao,
            UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.userDao = userDao;
    }

    @PostMapping
    public ResponseEntity<?> checkout(Principal principal) {
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found."));
            }

            ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());

            if (cart == null || cart.getItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Shopping cart is empty."));
            }

            Profile profile = profileDao.getByUserId(user.getId());

            if (profile == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User profile not found."));
            }

            // Create Order
            Order order = new Order();
            order.setUserId(user.getId());
            order.setDate(LocalDateTime.now());
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());
            order.setShippingAmount(BigDecimal.ZERO); // Customize if needed

            Order savedOrder = orderDao.create(order);

            // Insert order line items
            for (ShoppingCartItem cartItem : cart.getItems().values()) {
                OrderLineItem lineItem = new OrderLineItem();
                lineItem.setOrderId(savedOrder.getOrderId());
                lineItem.setProductId(cartItem.getProductId());
                lineItem.setQuantity(cartItem.getQuantity());
                lineItem.setSalesPrice(cartItem.getProduct().getPrice());

                BigDecimal subTotal = cartItem.getProduct().getPrice()
                        .multiply(new BigDecimal(cartItem.getQuantity()));

                BigDecimal discountAmount = subTotal.multiply(cartItem.getDiscountPercent());

                lineItem.setDiscount(discountAmount);

                orderLineItemDao.create(lineItem);
            }

            // Clear shopping cart
            shoppingCartDao.clearCart(user.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Order created successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not complete checkout."));
        }
    }

    //New features
}
