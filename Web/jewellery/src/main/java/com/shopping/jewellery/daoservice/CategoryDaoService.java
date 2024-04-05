package com.shopping.jewellery.daoservice;


import com.shopping.jewellery.dto.CategoryDTO;
import com.shopping.jewellery.entity.Category;
import com.shopping.jewellery.mapper.CategoryMapper;
import com.shopping.jewellery.repository.CategoryRepository;
import com.shopping.jewellery.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryDaoService implements CategoryService {
    @Autowired
    private CategoryRepository repository;

    @Autowired
    private CategoryMapper mapper;

    public String addCategory(CategoryDTO category) {
        repository.save(mapper.dtoToEntity(category));
        return "Category Added";
    }

    public String removeCategory(int categoryId) {
        return null;
    }

    public String updateCategory(int categoryId) {
        return null;
    }

    public CategoryDTO searchCategoryByName(String name) {
        return null;
    }

    public List<CategoryDTO> getAll() {
        List<Category> ls = repository.findAll();
        return ls.stream().map(p -> mapper.entityToDto(p)).toList();
    }

    public CategoryDTO searchCategoryById(int id) {
        return null;
    }

}
