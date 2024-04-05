package com.shopping.jewellery.mapper;

import com.shopping.jewellery.dto.CartDTO;
import com.shopping.jewellery.entity.Cart;
import com.shopping.jewellery.entity.CartItem;
import com.shopping.jewellery.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CartMapper {
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mappings({@Mapping(target = "cartItemQuantity", ignore = true), @Mapping(target = "cartTotalPrice", ignore = true), @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserIdToUser"), @Mapping(target = "cartItems", ignore = true), @Mapping(target = "active", ignore = true)})
    Cart dtoToEntity(CartDTO cartDto);

    @Mappings({@Mapping(target = "userId", source = "user.userId"), @Mapping(target = "productIds", source = "cartItems", qualifiedByName = "mapProductsToIds"), @Mapping(target = "quantity", ignore = true)})
    CartDTO entityToDto(Cart cart);

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(int userId) {
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    @Named("mapProductsToIds")
    default List<Integer> mapProductsToIds(List<CartItem> products) {
        return products.stream().map(CartItem::getProductId).toList();
    }
}
