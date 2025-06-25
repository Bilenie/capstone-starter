package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao {
    ShoppingCart getByUserId(int userId);

    // add additional method signatures here
    ShoppingCartItem addItem(int userId, int productId);

    void updateQuantity(int userId, int productId, int quantity);//shopping cart item object or quantity

    boolean clearCart(int userId);
    void removeItem(int userId,int productId);
}
