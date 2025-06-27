package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.UserDao;
import org.yearup.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlUserDao extends MySqlDaoBase implements UserDao {

    // Constructor: receives the DataSource and passes it to the parent class for database access
    @Autowired
    public MySqlUserDao(DataSource dataSource) {
        super(dataSource);
    }

    // Create a new user in the database (with hashed password) and return the saved user object
    @Override
    public User create(User newUser) {
        String sql = "INSERT INTO users (username, hashed_password, role) VALUES (?, ?, ?)";
        String hashedPassword = new BCryptPasswordEncoder().encode(newUser.getPassword());

        try (Connection connection = getConnection()) {

            // Prepare statement to insert user and get the generated ID
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newUser.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, newUser.getRole());

            ps.executeUpdate();

            // Fetch the created user and return it with password blanked out
            User user = getByUserName(newUser.getUsername());
            user.setPassword("");

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get a list of all users in the database
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet row = statement.executeQuery();

            // Map each row to a User object and add to the list
            while (row.next()) {
                User user = mapRow(row);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    // Retrieve a single user by their ID
    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet row = statement.executeQuery();

            // If a user is found, map and return it
            if (row.next()) {
                User user = mapRow(row);
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Retrieve a user by their username
    @Override
    public User getByUserName(String username) {
        String sql = "SELECT * " +
                " FROM users " +
                " WHERE username = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet row = statement.executeQuery();
            if (row.next()) {

                User user = mapRow(row);
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

    // Get user ID by username (or return -1 if not found)
    @Override
    public int getIdByUsername(String username) {
        User user = getByUserName(username);

        if (user != null) {
            return user.getId();
        }

        return -1;
    }

    // Check if a username exists in the database
    @Override
    public boolean exists(String username) {
        User user = getByUserName(username);
        return user != null;
    }

    // Helper method to convert a database row into a User object
    private User mapRow(ResultSet row) throws SQLException {
        int userId = row.getInt("user_id");
        String username = row.getString("username");
        String hashedPassword = row.getString("hashed_password");
        String role = row.getString("role");

        return new User(userId, username, hashedPassword, role);
    }
}
