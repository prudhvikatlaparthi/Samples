package com.shopping.jewellery.repository;

import com.shopping.jewellery.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findCartItemByProductIdAndCartCartId(int productId, int cartId);

    List<CartItem> findAllCartItemsByCartCartId(int cartId);

    void deleteById(long id);
}
