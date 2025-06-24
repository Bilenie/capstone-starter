package org.yearup.data.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.yearup.models.Product;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // automatically start our test for us --idk
class MySqlProductDaoTest extends BaseDaoTestClass {
    private MySqlProductDao dao;

    @BeforeEach
    public void setup()
    {
        // Set up the real test database connection (adjust this to my DB)
        BasicDataSource dataSource= new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/easyshop");
        dataSource.setUsername("root");
        dataSource.setPassword("yearup");

        // Create the DAO with this initialized dataSource
        dao = new MySqlProductDao(dataSource);
    }

    @Test
    public void getById_shouldReturn_theCorrectProduct() {
        // arrange
        int productId = 1;
        Product expected = new Product() {
            {
            setProductId(1);
            setName("Smartphone");
            setPrice(new BigDecimal("499.99"));
            setCategoryId(1);
            setDescription("A powerful and feature-rich smartphone for all your communication needs.");
            setColor("Black");
            setStock(50);
            setFeatured(false);
            setImageUrl("smartphone.jpg");
        }
        };

        // act
        var actual = dao.getById(productId);

        // assert
        assertEquals(expected.getPrice(), actual.getPrice(), "Because I tried to get product 1 from the database.");
    }

    @Test
    void listByCategoryId_shouldReturnNonEmptyList() {
        // Arrange
        int catId = 1;
        // Act
        List<Product> list = dao.listByCategoryId(catId);
        // Assert
        assertFalse(list.isEmpty(), "Expected products in category 1");
        assertTrue(list.stream().allMatch(p -> p.getCategoryId() == catId));
    }

    @Test
    void search_shouldFilterByCategoryAndColor() {
        // Arrange
        Integer catId = 1;
        BigDecimal min = null, max = null;
        String clr = "Black";
        // Act
        List<Product> results = dao.search(catId, min, max, clr);
        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(p ->
                p.getCategoryId() == catId && p.getColor().equalsIgnoreCase(clr)
        ));
    }

    @Test
    void create_update_delete_lifecycle() {
        // Arrange: create
        Product temp = new Product();
        temp.setName("UT Product");
        temp.setPrice(new BigDecimal("9.99"));
        temp.setCategoryId(1);
        temp.setDescription("Test");
        temp.setColor("Blue");
        temp.setImageUrl("img.jpg");
        temp.setStock(5);
        temp.setFeatured(false);

        Product created = dao.create(temp);
        assertTrue(created.getProductId() > 0);
        assertEquals("UT Product", created.getName());

        // Act & Assert: update
        created.setName("UT Product Updated");
        assertDoesNotThrow(() -> dao.update(created.getProductId(), created));

        Product updated = dao.getById(created.getProductId());
        assertEquals("UT Product Updated", updated.getName());

        // Act & Assert: delete
        assertDoesNotThrow(() -> dao.delete(created.getProductId()));
        Product afterDelete = dao.getById(created.getProductId());
        assertNull(afterDelete, "getById should return null after deletion");
    }

}