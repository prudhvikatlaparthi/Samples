package com.shopping.jewellery.service;

import com.shopping.jewellery.dto.UserDTO;

public interface UserService {
	//use throws for throwing appropriate Exceptions with functions

	public String registerCustomer(UserDTO userDTO);

	public String updateCustomer(UserDTO userDTO);

	public UserDTO getByMobile(long mobile);

//	public CartDTO findCartByUserId(int userId);

	public UserDTO signIn(String userName, String password);

	public String signOut();

}
