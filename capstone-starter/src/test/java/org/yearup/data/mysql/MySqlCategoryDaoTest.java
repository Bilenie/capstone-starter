package org.yearup.data.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.yearup.models.Category;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // automatically start our test for us --idk
class MySqlCategoryDaoTest extends BaseDaoTestClass {
    private MySqlCategoryDao categoryDao;

    @BeforeEach
    public void setup() {

        // Set up the real test database connection (adjust this to my DB)
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/easyshop");
        dataSource.setUsername("root");
        dataSource.setPassword("yearup");

        // Create the DAO with this initialized dataSource
        categoryDao = new MySqlCategoryDao(dataSource);
    }

    @Test
    public void getAllCategories_shouldReturnList() {
        // Arrange
        List<Category> categories = categoryDao.getAllCategories();

        //Act
        assertNotNull(categories); // Assert: result is not null
        assertTrue(categories.size() > 0); // Assert: at least one category exists
    }

    @Test
    public void getById_shouldReturnCategory() {
        Category category = categoryDao.getById(1); // make sure this ID exists in your DB

        assertNotNull(category);
        assertEquals(1, category.getCategoryId());
    }

    @Test
    public void create_shouldInsertCategory() {

        // Arrange
        Category newCategory = new Category();
        newCategory.setName("Test Category");
        newCategory.setDescription("Used for testing");

        // Act
        Category result = categoryDao.create(newCategory);

        // Assert
        assertTrue(result.getCategoryId() > 0, "Should return a generated ID > 0");// Should return the new ID
        assertEquals("Test Category", result.getName(), "Name should match");
        assertEquals("Used for testing", result.getDescription(), "Description should match");
    }

    @Test
    public void delete_shouldThrowWhenIdNotFound() {
        // Arrange
        int nonExistentId = 99;

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            categoryDao.delete(nonExistentId);
        });

        // Assert
        assertEquals(
                "Category ID 99 not found. Nothing was deleted.",
                ex.getMessage(),
                "Should report that the ID was not found"
        );
    }

    @Test
    public void delete_shouldSucceedWhenIdExists() {
        // Arrange
        // 1) Insert a temp category to delete
        Category temp = new Category();
        temp.setName("Temp");
        temp.setDescription("To be deleted");
        Category category = categoryDao.create(temp);  // now temp.getCategoryId() > 0

        // Act
        // This should not throw
        assertDoesNotThrow(() -> categoryDao.delete(category.getCategoryId()));

        // Assert
        // Optionally, verify that findById now throws or returns null
        RuntimeException notFound = assertThrows(RuntimeException.class, () -> {
            categoryDao.getById(category.getCategoryId());
        });
        assertTrue(notFound.getMessage().contains("not found"), "After deletion, findById should report not found");
    }
}