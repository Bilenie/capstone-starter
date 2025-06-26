package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")
@CrossOrigin
public class ProductsController {
    //set attribute
    private ProductDao productDao;

    //Constructor
    @Autowired
    public ProductsController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @GetMapping()// get all product
    @PreAuthorize("permitAll()") // allowed to all
    public List<Product> search(@RequestParam(name = "cat", required = false) Integer categoryId,
                                @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                                @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
                                @RequestParam(name = "color", required = false) String color
    ) {
        try {
            return productDao.search(categoryId, minPrice, maxPrice, color);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @GetMapping("{id}") //Get product by the ID specify
    @PreAuthorize("permitAll()") // open to users and admin
    public Product getById(@PathVariable int id) {
        try {
            var product = productDao.getById(id);

            if (product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            return product;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PostMapping// remove the () not using the specify path
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Only admins can Create
    public Product addProduct(@RequestBody Product product) {
        try {
            return productDao.create(product);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PutMapping("{id}") //This update a product to the database where the ID is specify in the path.
    @PreAuthorize("hasRole('ROLE_ADMIN')")// Only admins can update
    public void updateProduct(@PathVariable int id, @RequestBody Product product) {
        try {
            productDao.create(product);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @DeleteMapping("{id}") // This deletes a product from the database by its ID
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Only admins can delete
    public void deleteProduct(@PathVariable int id) {
        try {
            var product = productDao.getById(id);

            if (product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            productDao.delete(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
