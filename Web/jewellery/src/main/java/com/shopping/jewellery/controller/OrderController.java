package com.shopping.jewellery.controller;

import com.shopping.jewellery.daoservice.OrderDaoService;
import com.shopping.jewellery.dto.OrderDTO;
import com.shopping.jewellery.dto.PlaceOrderDTO;
import com.shopping.jewellery.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/order")
public class OrderController {

    @Autowired
    private OrderDaoService service;


    @PostMapping("/placeOrder")
    Integer placeOrder(@RequestBody PlaceOrderDTO placeOrderDTO){
        return service.placeOrder(placeOrderDTO);
    }

    @PostMapping("/getOrdersByUserId")
    List<OrderDTO> getOrdersByUserId(@RequestBody UserDTO userDTO){
        return service.getOrdersByUserId(userDTO.getUserId());
    }

    @PostMapping("/getOrderDetailsByOrderId")
    OrderDTO getOrderDetailsByOrderId(@RequestBody OrderDTO orderDTO){
        return service.getOrderDetailsByOrderId(orderDTO.getOrderId());
    }

}
