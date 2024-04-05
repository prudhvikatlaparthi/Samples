package com.shopping.jewellery.mapper;

import com.shopping.jewellery.dto.CategoryDTO;
import com.shopping.jewellery.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category dtoToEntity(CategoryDTO categoryDto);

    CategoryDTO entityToDto(Category category);
}
