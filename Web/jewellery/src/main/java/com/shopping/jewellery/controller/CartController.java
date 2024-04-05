package com.shopping.jewellery.controller;

import com.shopping.jewellery.daoservice.CartDaoService;
import com.shopping.jewellery.dto.CartDTO;
import com.shopping.jewellery.dto.CartItemDTO;
import com.shopping.jewellery.dto.ProductDTO;
import com.shopping.jewellery.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/cart")
public class CartController {

    @Autowired
    private CartDaoService service;

    @PostMapping("/addCartItem")
    public int addCartItem(@RequestBody CartDTO cartDTO) {
        return service.addCartItem(cartDTO);
    }

    @PostMapping("/getCartIdByUserId")
    public int getCartIdByUserId(@RequestBody UserDTO userDTO) {
        return service.getCartIdByUserId(userDTO.getUserId());
    }

    @PostMapping("/updateCartItem")
    public String updateCartItem(@RequestBody CartItemDTO cartItemDTO) {
        return service.updateCartItem(cartItemDTO);
    }

    @PostMapping("/deleteCartItem")
    public String deleteCartItem(@RequestBody CartItemDTO cartItemDTO) {
        return service.deleteCartItem(cartItemDTO);
    }

    @PostMapping("/getCartItems")
    public List<CartItemDTO> getCartItems(@RequestBody UserDTO userDTO) {
        return service.getCartItems(userDTO.getUserId());
    }

}
