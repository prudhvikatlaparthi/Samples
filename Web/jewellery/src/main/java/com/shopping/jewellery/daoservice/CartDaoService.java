package com.shopping.jewellery.daoservice;


import com.shopping.jewellery.dto.CartDTO;
import com.shopping.jewellery.dto.CartItemDTO;
import com.shopping.jewellery.entity.Cart;
import com.shopping.jewellery.entity.CartItem;
import com.shopping.jewellery.entity.Product;
import com.shopping.jewellery.mapper.CartMapper;
import com.shopping.jewellery.mapper.ProductMapper;
import com.shopping.jewellery.repository.CartItemRepository;
import com.shopping.jewellery.repository.CartRepository;
import com.shopping.jewellery.repository.ProductRepository;
import com.shopping.jewellery.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CartDaoService implements CartService {

    @Autowired
    private CartRepository repository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartMapper mapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public int addCartItem(CartDTO cartDTO) {
        Cart entity = mapper.dtoToEntity(cartDTO);
        Cart cart = repository.save(entity);
        for (Integer p : cartDTO.getProductIds()) {
            Product prod = productRepository.findById(p).get();
            if (prod.getQuantity() == 0) continue;
            int quantity = cartDTO.getQuantity();
            prod.setQuantity(prod.getQuantity() - quantity);
            CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartCartId(p, cartDTO.getCartId());
            if (cartItem != null) {
                cartItem.setQuantity(cartItem.getQuantity() + cartDTO.getQuantity());
                cartItem.setPrice(cartItem.getQuantity() * prod.getPrice());
                cartItemRepository.save(cartItem);
            } else {
                CartItem newCart = new CartItem();
                newCart.setProductId(prod.getProductId());
                newCart.setCart(cart);
                newCart.setQuantity(quantity);
                newCart.setPrice(newCart.getQuantity() * prod.getPrice());
                cartItemRepository.save(newCart);
            }
            productRepository.save(prod);
        }
        updateCart(cart.getCartId());
        return cart.getCartId();
    }

    @Transactional
    public String deleteCartItem(CartItemDTO cartDTO) {
        CartItem cartItem = cartItemRepository.findById(cartDTO.getId()).get();
        Product pro = productRepository.findById(cartItem.getProductId()).get();

        if (cartDTO.isAll()) {
            cartItemRepository.deleteById(cartItem.getId());
            pro.setQuantity(pro.getQuantity() + cartItem.getQuantity());
        } else {
            if (cartItem.getQuantity() == 1) {
                cartItemRepository.deleteById(cartItem.getId());
                pro.setQuantity(pro.getQuantity() + 1);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() - cartDTO.getQuantity());
                pro.setQuantity(pro.getQuantity() + cartDTO.getQuantity());
                cartItemRepository.save(cartItem);
            }
        }

        productRepository.save(pro);
        updateCart(cartItem.getCart().getCartId());
        return "Cart Item Deleted";
    }

    public String updateCartItem(CartItemDTO cartDTO) {
        CartItem cartItem = cartItemRepository.findById(cartDTO.getId()).get();
        Product pro = productRepository.findById(cartItem.getProductId()).get();
        int quantity = cartDTO.getQuantity();
        pro.setQuantity(pro.getQuantity() - quantity);
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);
        productRepository.save(pro);
        updateCart(cartItem.getCart().getCartId());
        return "Cart Item Updated";
    }

    private void updateCart(int cartId) {
        Cart cart = repository.findById(cartId).get();
        List<CartItem> cartItems = cartItemRepository.findAllCartItemsByCartCartId(cartId);
        AtomicInteger totalQuantity = new AtomicInteger();
        AtomicReference<Double> totalPrice = new AtomicReference<>((double) 0);
        cartItems.forEach(c -> {
            totalQuantity.set(totalQuantity.get() + c.getQuantity());
            totalPrice.set(totalPrice.get() + (c.getPrice() * c.getQuantity()));

        });
        cart.setCartTotalPrice(totalPrice.get());
        cart.setCartItemQuantity(totalQuantity.get());
        repository.save(cart);
    }

    public List<CartItemDTO> getCartItems(int userId) {
        try {
            Cart cart = repository.findCartByUserUserId(userId);
            if (cart == null || cart.getCartId() == 0) return new ArrayList<>();
            List<CartItem> cartItems = cartItemRepository.findAllCartItemsByCartCartId(cart.getCartId());
            return cartItems.stream().map(p -> {
                Product pro = productRepository.findById(p.getProductId()).get();
                return new CartItemDTO(p.getId(), p.getQuantity(), p.getProductId(), p.getPrice(), pro.getProductName(), pro.getQuantity());
            }).toList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int getCartIdByUserId(int userId) {
        try {
            Cart cart = repository.findCartByUserUserId(userId);
            return cart != null ? cart.getCartId() : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
