package com.shopping.jewellery.repository;

import com.shopping.jewellery.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllProductsByCategoryCategoryId(int categoryId);
}
