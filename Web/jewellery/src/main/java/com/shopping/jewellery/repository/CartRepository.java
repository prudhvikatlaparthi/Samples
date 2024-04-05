package com.shopping.jewellery.repository;

import com.shopping.jewellery.entity.Cart;
import com.shopping.jewellery.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    //    void deleteByProducts(Product product);
    Cart findCartByIsActive(boolean isActive);

    List<CartItem> findAllCartItemsByCartId(int id);

    Cart findCartByUserUserId(int userId);


    void deleteAllCartItemsByCartId(int id);
}
