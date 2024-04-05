package com.shopping.jewellery.mapper;

import com.shopping.jewellery.dto.ProductDTO;
import com.shopping.jewellery.entity.Category;
import com.shopping.jewellery.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mappings({
            @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryIdToCategory")
    })
    Product dtoToEntity(ProductDTO productDto);

    @Mappings({
            @Mapping(target = "categoryId", source = "category.categoryId")
    })
    ProductDTO entityToDto(Product product);

    @Named("mapCategoryIdToCategory")
    default Category mapCategoryIdToCategory(int categoryId) {
        Category category = new Category();
        category.setCategoryId(categoryId);
        return category;
    }
}
