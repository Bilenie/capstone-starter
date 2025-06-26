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

@RestController                      // This class answers HTTP requests from the internet
@RequestMapping("/cart")            // Every URL here starts with /cart
@CrossOrigin                        // Let frontend websites talk to this backend
@PreAuthorize("permitAll()") // Only logged-in users can use these methods
public class ShoppingCartController {

// Set attributes a shopping cart requires

    private ShoppingCartDao shoppingCartDao; // Talks to the shopping cart table
    private UserDao userDao;//Talks to the user table
    private ProductDao productDao; // Talks to the product table

    //Generate Constructor to sets up the tools, so I can use them
    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

// each method in this controller requires a Principal object as a parameter

    @GetMapping// Get everything in the user's cart
    public ShoppingCart getCart(Principal principal) {
        try {
            // get the currently logged-in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shopping cartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added

    @PostMapping("/products/{productId}")
    public ShoppingCart addProductToCart(Principal principal, @PathVariable int productId) {
        try {
            // get the currently logged-in username
            String userName = principal.getName();

            // find database user by userId
            User user = userDao.getByUserName(userName);

            //add validation if user is null
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "User not found");
            }
            int userId = user.getId();

            shoppingCartDao.addItem(userId, productId);//add item to the cart

            return shoppingCartDao.getByUserId(userId);//return the updated cart
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }

    }

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated

    @PutMapping("/products/{id}") //This update a product to the database where the ID is specify in the path.
    public ShoppingCart updateProduct(Principal principal, @PathVariable int productId, @RequestBody ShoppingCartItem shoppingCartItem) {
        try {
            // get the currently logged-in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "User not found");
            }
            int userId = user.getId();

            shoppingCartDao.updateQuantity(userId, productId, shoppingCartItem.getQuantity());

            return shoppingCartDao.getByUserId(userId);//return the updated cart

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping() // This deletes a product from the shopping cart by its ID
    public ShoppingCart deleteProductFromCart(Principal principal) {
        try {
            // get the currently logged-in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "User not found");
            }
            shoppingCartDao.clearCart(userId);
            return shoppingCartDao.getByUserId(userId);//return the updated cart

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @DeleteMapping("{id}") // This deletes a product from the database by its ID
    public ShoppingCart deleteProductFromCartById(Principal principal, @PathVariable("id") int productId) {
        try {
            // get the currently logged-in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "User not found");
            }
            shoppingCartDao.removeItem(userId, productId);
            return shoppingCartDao.getByUserId(userId);//return the updated cart
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
