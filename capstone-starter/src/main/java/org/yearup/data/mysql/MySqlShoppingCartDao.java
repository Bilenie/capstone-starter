package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    private ProductDao productDao;

    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {

        //Instantiate a shopping cart to put our product selected
        ShoppingCart shoppingCart = new ShoppingCart();

        String sql = """
                   SELECT s.* 
                   FROM shopping_cart s
                   WHERE user_id = ?;
                """;
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            // Go through each row returned from the database
            while (resultSet.next()) {

                //getting the column
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quantity");

                // load Product via productDao.getById(productId)
                Product product = productDao.getById(productId);

                // create a ShoppingCartItem with product, quantity,discount percent from the shopping cart item class
                ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                shoppingCartItem.setProduct(product);
                shoppingCartItem.setQuantity(quantity);
                shoppingCartItem.setDiscountPercent(BigDecimal.ZERO);

                // in the shoppingCartItem blueprint the getLineTotal method do the calculation
                shoppingCartItem.getLineTotal();

                // put it into shoppingCart.getItems().put(...)
                //shoppingCart.getItems().put(productId,shoppingCartItem);
                shoppingCart.add(shoppingCartItem);

                //  call the getTotal method to do the total price calculation on the cart
                shoppingCart.getTotal();

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving categories", e);
        }
        return shoppingCart;

    }

    // added additional method signatures here

    public ShoppingCartItem addItem(int userId, int productId) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ? , ?) ON DUPLICATE KEY UPDATE quantity = quantity + 1;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters for name and description
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setInt(3, shoppingCartItem.getQuantity());

            // Run the SQL insert =>implement the row affected/*
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                Product product = productDao.getById(productId);
                shoppingCartItem.setProduct(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert category", e);
        }
        return shoppingCartItem;
    }


    public void updateQuantity(int userId, int productId, int quantity) {
        String sql = """
                UPDATE 
                   shopping_cart
                SET 
                   quantity = ?
                WHERE 
                    user_id = ? 
                  AND 
                    product_id = ?;
                
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean clearCart(int userId) {

        String sql = """ 
                
                 DELETE FROM shopping_cart
                 WHERE product_id = ?;
                
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}