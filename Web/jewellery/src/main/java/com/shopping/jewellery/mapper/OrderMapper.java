package com.shopping.jewellery.mapper;

import com.shopping.jewellery.dto.OrderDTO;
import com.shopping.jewellery.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mappings({
            @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserIdToUser"),
    })
    Order dtoToEntity(OrderDTO orderDto);



    @Mapping(target = "userId", ignore = true)
    OrderDTO entityToDto(Order order);

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(int userId) {
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    @Named("mapCartsToIds")
    default List<Integer> mapCartsToIds(List<OrderItem> orders) {
        return orders.stream().map(p -> p.getOrder().getOrderId()).toList();
    }
}
