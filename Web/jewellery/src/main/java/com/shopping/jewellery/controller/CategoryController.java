package com.shopping.jewellery.controller;

import com.shopping.jewellery.daoservice.CategoryDaoService;
import com.shopping.jewellery.daoservice.ProductDaoService;
import com.shopping.jewellery.dto.CategoryDTO;
import com.shopping.jewellery.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    @Autowired
    private CategoryDaoService service;

    @PostMapping("/add")
    public String addCategory(@RequestBody CategoryDTO categoryDTO) {
        return service.addCategory(categoryDTO);
    }

    @GetMapping("/getAll")
    public List<CategoryDTO> getAll(){
        return service.getAll();
    }
}
