package com.shopping.jewellery.daoservice;


import com.shopping.jewellery.dto.CategoryDTO;
import com.shopping.jewellery.dto.ProductDTO;
import com.shopping.jewellery.mapper.CategoryMapper;
import com.shopping.jewellery.mapper.ProductMapper;
import com.shopping.jewellery.repository.ProductRepository;
import com.shopping.jewellery.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductDaoService implements ProductService {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductMapper mapper;

    public ProductDTO getById(int id) {
        return null;
    }

    public ProductDTO updateProduct(ProductDTO productDTO) {
        return null;
    }

    public boolean deleteProduct(ProductDTO productDTO) {
        return false;
    }

    public String addProduct(ProductDTO productDTO) {
        repository.save(mapper.dtoToEntity(productDTO));
        return "Product Added";
    }

    public List<ProductDTO> findAll() {
        return repository.findAll().stream().map(p -> mapper.entityToDto(p)).toList();
    }

    public List<ProductDTO> findAllProductsByCategoryCategoryId(CategoryDTO categoryDTO) {
        return repository.findAllProductsByCategoryCategoryId(categoryDTO.getCategoryId()).stream().map(p -> mapper.entityToDto(p)).toList();
    }

//    public List<ProductDTO> getProductByCategoryName(int categoryName) {
//        return null;
//    }

}
