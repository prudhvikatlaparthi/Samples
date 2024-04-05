package com.shopping.jewellery.repository;

import com.shopping.jewellery.entity.Cart;
import com.shopping.jewellery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByMobileNumber(long mobileNumber);

    User findByUserNameAndPassword(String userName, String password);

    List<Cart> findCartByUserId(int userId);
}
