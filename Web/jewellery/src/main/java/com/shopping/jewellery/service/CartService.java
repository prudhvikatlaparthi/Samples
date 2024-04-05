package com.shopping.jewellery.service;


import com.shopping.jewellery.dto.CartDTO;

public interface CartService {
	
	//use throws for throwing appropriate Exceptions with functions
	public int addCartItem(CartDTO cartDTO);

//	public String deleteProduct(ProductDTO productDTO);
}
