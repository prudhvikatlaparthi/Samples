package com.shopping.jewellery.repository;

import com.shopping.jewellery.entity.CartItem;
import com.shopping.jewellery.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {


    List<OrderItem> findAllOrderItemsByOrderOrderId(int orderId);
}
