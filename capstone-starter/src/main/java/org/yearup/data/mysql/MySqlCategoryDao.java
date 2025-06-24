package org.yearup.data.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {

    //private BasicDataSource dataSource;

    @Autowired
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        // SQL to get all columns from the categories table
        String sql = "SELECT * FROM categories";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Go through each row returned from the database
            while (resultSet.next()) {
                Category category = new Category();

                // Set the properties from the current row
                category.setCategoryId(resultSet.getInt("category_id")); // match column name exactly
                category.setName(resultSet.getString("name"));
                category.setDescription(resultSet.getString("description"));

                // Add it to the list
                categories.add(category);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving categories", e);
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                } else {
                    throw new RuntimeException(
                            "Category ID " + categoryId + " not found. Nothing was retrieved."
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving category with ID: " + categoryId, e);
        }

    }


    @Override
    public Category create(Category category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set parameters for name and description
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());

            // Run the SQL insert
            stmt.executeUpdate();

            // Get the newly generated ID from the database
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                category.setCategoryId(rs.getInt(1));
            }

            return category;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert category", e);
        }

    }

    @Override
    public void update(int categoryId, Category category) {
        String sql = """
                    UPDATE categories
                    SET name = ?, description = ?
                    WHERE category_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the values in the query
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);  // the WHERE condition

            //  Actually execute the update
            int rowsAffected = stmt.executeUpdate();

            // Check if the update actually changed something
            if (rowsAffected == 0) {
                throw new RuntimeException("Category ID " + categoryId + " not found. Nothing was updated.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while updating category.", e);
        }
    }


    @Override
    public void delete(int categoryId) {

        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            int rowsAffected = stmt.executeUpdate(); // returns 0 if nothing deleted

            if (rowsAffected == 0) {
                throw new RuntimeException("Category ID " + categoryId + " not found. Nothing was deleted.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while deleting category.", e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
