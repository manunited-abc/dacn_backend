package nlu.dacn.dacn_backend.controller;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.dto.request.CartDTO;
import nlu.dacn.dacn_backend.dto.request.TokenAndIdsDTO;
import nlu.dacn.dacn_backend.dto.response.ResponMessenger;
import nlu.dacn.dacn_backend.service.impl.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/cart/laptops")
    public CartDTO getLaptopsFromCart(@RequestParam String token) {
        return cartService.findLaptopByUser(token);
    }

    @PostMapping("/cart/laptop/add")
    public ResponseEntity<?> addLaptopToCart(@RequestParam String token, @RequestParam Long laptopId, @RequestParam Integer quantity) {
        cartService.addLaptopToCart(token, laptopId, quantity);
        return new ResponseEntity<>(new ResponMessenger("Đã thêm thành công sản phẩm vào giỏ hàng"), HttpStatus.OK);
    }

    @DeleteMapping("/cart/laptop/remove")
    public ResponseEntity<?> removeLaptopInCart(@RequestBody TokenAndIdsDTO dto) {
        cartService.removeLaptopInCart(dto);
        return new ResponseEntity<>(new ResponMessenger("Đã xóa thành công sản phẩm khỏi giỏ hàng"), HttpStatus.OK);
    }

    @PutMapping("/cart/laptop/reduce")
    public ResponseEntity<?> reduceLaptopQuantityInCart(@RequestBody TokenAndIdsDTO dto) {
        cartService.reduceLaptopQuantity(dto);
        return new ResponseEntity<>(new ResponMessenger("Đã giảm số lượng sản phẩm thành công"), HttpStatus.OK);
    }
}
