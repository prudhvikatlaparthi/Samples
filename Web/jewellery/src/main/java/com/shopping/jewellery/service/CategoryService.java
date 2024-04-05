package com.shopping.jewellery.service;


import com.shopping.jewellery.dto.CategoryDTO;

public interface CategoryService {
	//use throws for throwing appropriate Exceptions with functions
	public String addCategory(CategoryDTO category);

	public String removeCategory(int categoryId);

	public String updateCategory(int categoryId);

	public CategoryDTO searchCategoryByName(String name);

	public CategoryDTO searchCategoryById(int id);

}
