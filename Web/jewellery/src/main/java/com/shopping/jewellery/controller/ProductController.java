package com.shopping.jewellery.controller;

import com.shopping.jewellery.daoservice.ProductDaoService;
import com.shopping.jewellery.dto.CategoryDTO;
import com.shopping.jewellery.dto.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductController {

    @Autowired
    private ProductDaoService service;

    @GetMapping("/getAll")
    public List<ProductDTO> findAll() {
        return service.findAll();
    }

    @PostMapping("/getProductsByCategoryId")
    public List<ProductDTO> findAllProductsByCategoryCategoryId(@RequestBody CategoryDTO categoryDTO) {
        return service.findAllProductsByCategoryCategoryId(categoryDTO);
    }

    @PostMapping("/add")
    public String addProduct(@RequestBody ProductDTO productDTO) {
        return service.addProduct(productDTO);
    }
}
