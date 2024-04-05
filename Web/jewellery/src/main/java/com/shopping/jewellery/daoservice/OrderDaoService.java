package com.shopping.jewellery.daoservice;


import com.shopping.jewellery.dto.OrderDTO;
import com.shopping.jewellery.dto.PlaceOrderDTO;
import com.shopping.jewellery.entity.*;
import com.shopping.jewellery.mapper.OrderMapper;
import com.shopping.jewellery.repository.*;
import com.shopping.jewellery.service.OrderService;
import com.shopping.jewellery.utils.OrderStatus;
import com.shopping.jewellery.utils.PaymentModeEnum;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderDaoService implements OrderService {
    //use throws for throwing appropriate Exceptions with functions

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderMapper mapper;

    public OrderDTO addOrders(OrderDTO ordersDTO) {
        return null;
    }

    public OrderDTO updateOrders(OrderDTO ordersDTO) {
        return null;
    }

    public boolean deleteOrders(OrderDTO ordersDTO) {
        return false;
    }

    public OrderDTO getById(int id) {
        return null;
    }

    public List<OrderDTO> findAll() {
        return null;
    }

    public List<OrderDTO> getOrderCustomerId(int customerId) {
        return null;
    }

    public List<OrderDTO> getOrderCustomerEmail(int customerEmail) {
        return null;
    }

    public List<OrderDTO> getOrderByStatus(String orderstatus) {
        return null;
    }

    public List<OrderDTO> getOrdersByUserId(int userId) {
        List<Order> orders = repository.findAllOrdersByUserUserId(userId);
        List<OrderDTO> response = new ArrayList<>();
        orders.forEach(order -> {
            OrderDTO ordersDTO = mapper.entityToDto(order);
            response.add(ordersDTO);
        });
        return response;
    }

    public OrderDTO getOrderDetailsByOrderId(int orderId) {
        Order order = repository.findById(orderId).get();
        return mapper.entityToDto(order);
    }

    @Transactional
    public Integer placeOrder(PlaceOrderDTO placeOrderDTO) {
        try {
            Cart cart = cartRepository.findCartByUserUserId(placeOrderDTO.getUserId());
            User user = userRepository.findById(placeOrderDTO.getUserId()).get();
            if (cart == null) return 0;
            List<CartItem> cartItems = cart.getCartItems();
            if (cartItems == null || cartItems.isEmpty()) return 0;
            Order order = new Order();
            order.setDate(LocalDateTime.now());
            order.setTotalQuantity(0);
            order.setTotalPrice(0);
            order.setUser(user);
            order.setStatus(OrderStatus.Placed);
            order.setPaymentMode(PaymentModeEnum.valueOf(placeOrderDTO.getPaymentMode().toUpperCase()));
            Order savedOrder = repository.save(order);
            cartItems.forEach(c -> {
                String productName = productRepository.findById(c.getProductId()).get().getProductName();
                OrderItem orderItem = new OrderItem(0L, savedOrder, c.getProductId(), productName, c.getQuantity(), c.getPrice());
                orderItemRepository.save(orderItem);
            });
            updateOrder(savedOrder.getOrderId());
            cartRepository.deleteAllCartItemsByCartId(cart.getCartId());
            return savedOrder.getOrderId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    private void updateOrder(int orderId) {
        try {
            Order order = repository.findById(orderId).get();
            List<OrderItem> orderItems = orderItemRepository.findAllOrderItemsByOrderOrderId(orderId);
            AtomicInteger totalQuantity = new AtomicInteger();
            AtomicReference<Double> totalPrice = new AtomicReference<>((double) 0);
            orderItems.forEach(c -> {
                totalQuantity.set(totalQuantity.get() + c.getQuantity());
                totalPrice.set(totalPrice.get() + (c.getPrice() * c.getQuantity()));

            });
            order.setTotalPrice(totalPrice.get());
            order.setTotalQuantity(totalQuantity.get());
            repository.save(order);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
