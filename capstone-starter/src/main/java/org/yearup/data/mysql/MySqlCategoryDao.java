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

    private BasicDataSource dataSource;

    @Autowired
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();
        String sql = " SELECT c.* FROM categories c";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            //run the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Loop through each row in the ResultSet.
            while (resultSet.next()) {
                // Create a new object.
                Category category = new Category();

                category.setCategoryId(resultSet.getInt("Category_id"));
                category.setName(resultSet.getString("name"));
                category.setDescription(resultSet.getString("description"));

                // Add the Film object to our list.
                categories.add(category);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        String sql = "SELECT c.category_id, c.name FROM categories c WHERE c.category_id = ?";

        Category category = new Category();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId );
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    category.setCategoryId(rs.getInt("Category_id"));
                    category.setName(rs.getString("name"));
                    category.setDescription(rs.getString("description"));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
