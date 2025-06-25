package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;
import java.util.Map;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    //Added this constructor
    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    // GET /cart - Get current user's shopping cart
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            String username = principal.getName();
            System.out.println("Authenticated username: " + username);

            User user = userDao.getByUserName(username);
            if (user == null)
            {
                System.out.println("User not found for username: " + username);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");
            }

            int userId = user.getId();
            System.out.println("User ID: " + userId);

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            if (cart == null)
            {
                System.out.println("No shopping cart found for userId: " + userId + ". Returning empty cart.");
                cart = new ShoppingCart(); // create empty cart to avoid 500
            }

            return cart;
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            e.printStackTrace(); // logs error in server console
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not retrieve shopping cart.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    // POST /cart/products/{productId} - Add product to cart or increase quantity by 1
    @PostMapping("/products/{productId}")
    public void addToCart(@PathVariable int productId, Principal principal)
    {
        try
        {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

            int userId = user.getId();

            // Check if product exists in DB
            if (productDao.getById(productId) == null)
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
            }

            ShoppingCartItem existingItem = shoppingCartDao.getItem(userId, productId);

            if (existingItem == null)
            {
                shoppingCartDao.addItem(userId, productId, 1);
            }
            else
            {
                int newQuantity = existingItem.getQuantity() + 1;
                shoppingCartDao.updateQuantity(userId, productId, newQuantity);
            }
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not add item to cart.");
        }
    }

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{productId}")
    public void updateCartItem(@PathVariable int productId, @RequestBody Map<String, Integer> body, Principal principal)
    {
        try
        {
            if (!body.containsKey("quantity"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'quantity' in request body.");

            int quantity = body.get("quantity");
            if (quantity < 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be zero or greater.");

            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

            int userId = user.getId();

            ShoppingCartItem existingItem = shoppingCartDao.getItem(userId, productId);

            if (existingItem != null)
            {
                if (quantity == 0)
                {
                    // Remove item if quantity set to 0
                    shoppingCartDao.updateQuantity(userId, productId, 0);
                    // Or you can create a separate removeItem method to delete the row entirely if needed
                }
                else
                {
                    shoppingCartDao.updateQuantity(userId, productId, quantity);
                }
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found in cart.");
            }
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not update item.");
        }
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    // DELETE /cart - Clear the entire cart
    @DeleteMapping
    public void clearCart(Principal principal)
    {
        try
        {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

            int userId = user.getId();
            shoppingCartDao.clearCart(userId);
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not clear shopping cart.");
        }
    }
}
