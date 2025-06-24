package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

@RestController// add the annotations to make this a REST controller
@RequestMapping("categories")// add the annotation to make this controller the endpoint for the following url
// http://localhost:8080/categories
@CrossOrigin// add annotation to allow cross site origin requests
public class CategoriesController {

    private CategoryDao categoryDao;
    private ProductDao productDao;

    //Best practice constructor Dependency injection
    @Autowired // create an Autowired controller to inject the categoryDao and ProductDao
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }


    @GetMapping   // add the appropriate annotation for a get action
    public List<Category> getAll() {
        // find and return all categories
        try
        {
            var category = categoryDao.getAllCategories();

            if(category == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            // Get products by categoryId
            return category;
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }

    }

    @GetMapping("{id}")// add the appropriate annotation for a get action
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        try
        {
            var category = categoryDao.getById(id);

            if(category == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            // Get products by categoryId
            return categoryDao.getById(id);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId
        return productDao.listByCategoryId(categoryId);

    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category
        try
        {
            return categoryDao.create(category);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }

    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)// Return 204 No Content on success
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // Verify it exists (will throw 404 if missing)
        try {
            categoryDao.getById(id);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Category ID " + id + " not found", ex);
        }

        // update
        try {
            categoryDao.update(id, category);
        } catch (RuntimeException ex) {
            // If your DAO threw because rowsAffected==0, it's unexpected (we already checked exists),
            // but we still map it to a 500.
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update category " + id,ex);
        }
    }

    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)// Tell Spring to return 204 if this method completes normally
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        try
        {
            var category = categoryDao.getById(id);

            if(category== null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            categoryDao.delete(id);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}

