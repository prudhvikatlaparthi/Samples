package com.shopping.jewellery.daoservice;

import com.shopping.jewellery.dto.UserDTO;
import com.shopping.jewellery.entity.User;
import com.shopping.jewellery.mapper.UserMapper;
import com.shopping.jewellery.repository.UserRepository;
import com.shopping.jewellery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDaoService implements UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper userMapper;

    public String registerCustomer(UserDTO userDTO) {
        try {
            repository.save(userMapper.dtoToEntity(userDTO));
        } catch (Exception e) {
            return e.getMessage();
        }
        return "User Saved";
    }

    public String updateCustomer(UserDTO userDTO) {
        if (userDTO.getUserId() == 0) return "User not found";
        repository.save(userMapper.dtoToEntity(userDTO));
        return "User Updated";
    }

    public UserDTO getByMobile(long mobile) {
        User user = repository.findByMobileNumber(mobile);
        if (user != null) return userMapper.entityToDto(user);
        return null;
    }

   /* public CartDTO findCartByUserId(int userId) {
        List<Cart> cart = repository.findCartByUserId(userId);
        if (cart != null) return CartMapper.mapToDTO(cart.get(0));
        return null;
    }*/

    public UserDTO signIn(String userName, String password) {
        User user = repository.findByUserNameAndPassword(userName, password);
        if (user != null) return userMapper.entityToDto(user);
        return null;
    }

    public String signOut() {
        return null;
    }

}
