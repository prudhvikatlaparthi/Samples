package com.shopping.jewellery.controller;

import com.shopping.jewellery.daoservice.UserDaoService;
import com.shopping.jewellery.dto.CartDTO;
import com.shopping.jewellery.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserDaoService service;

    @PostMapping("/add")
    public String register(@RequestBody UserDTO dto) {
        return service.registerCustomer(dto);
    }

    @PostMapping("/update")
    public String update(@RequestBody UserDTO dto) {
        return service.updateCustomer(dto);
    }

    @PostMapping("/authenticate")
    public UserDTO signIn(@RequestBody UserDTO dto) {
        return service.signIn(dto.getUserName(), dto.getPassword());
    }
}
