package org.yearup.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.*;

import java.security.Principal;
import java.util.List;

@RestController// added the annotations to make this a REST controller
@RequestMapping("profile")// added the annotation to make this controller the endpoint for the following url
@CrossOrigin// added annotation to allow cross site origin requests =>// http://localhost:8080/profile
@PreAuthorize("permitAll()") // users can use these methods
public class profileController {

    private ProfileDao profileDao;
    private UserDao userDao;


    @Autowired
    public profileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping   // add the appropriate annotation for a get action
    public Profile getProfileByUserId(Principal principal) {
        // find and return all categories
        try {
            // get the currently logged-in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shopping cartDao to get all items in the cart and return the cart
            return profileDao.getByUserId(userId);

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }

    }


    @PutMapping //This update a profiles to the database
    public Profile updateProduct(Principal principal, @RequestBody Profile profile) {
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
            System.out.println(profile.getFirstName() + " First name ");

            return profileDao.update(userId, profile);//return the updated profile

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }


}
