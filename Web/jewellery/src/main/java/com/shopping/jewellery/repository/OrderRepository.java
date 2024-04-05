package com.shopping.jewellery.repository;

import com.shopping.jewellery.entity.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
        List<Order> findAllOrdersByUserUserId(int userId);
}
