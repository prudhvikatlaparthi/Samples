package com.shopping.jewellery.service;


import com.shopping.jewellery.dto.CategoryDTO;
import com.shopping.jewellery.dto.ProductDTO;

import java.util.List;

public interface ProductService {
	//use throws for throwing appropriate Exceptions with functions
	public ProductDTO getById(int id);

	public ProductDTO updateProduct(ProductDTO productDTO);

	public boolean deleteProduct(ProductDTO productDTO);

	public String addProduct(ProductDTO productDTO);

	public List<ProductDTO> findAll();

//	public List<ProductDTO> findAllByCategory(CategoryDTO categoryDTO);

//	public List<ProductDTO> getProductByCategoryName(int categoryName);

}
