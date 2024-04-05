package com.shopping.jewellery.service;


import com.shopping.jewellery.dto.OrderDTO;

import java.util.List;

public interface OrderService {
	//use throws for throwing appropriate Exceptions with functions
	public OrderDTO addOrders(OrderDTO ordersDTO);

	public OrderDTO updateOrders(OrderDTO ordersDTO);

	public boolean deleteOrders(OrderDTO ordersDTO);

	public OrderDTO getById(int id);

	public List<OrderDTO> findAll();

	public List<OrderDTO> getOrderCustomerId(int customerId);

	public List<OrderDTO> getOrderCustomerEmail(int customerEmail);

	public List<OrderDTO> getOrderByStatus(String orderstatus);
}
