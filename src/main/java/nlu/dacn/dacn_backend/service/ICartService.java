package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.dto.request.CartDTO;
import nlu.dacn.dacn_backend.dto.request.TokenAndIdsDTO;

public interface ICartService {
   CartDTO findLaptopByUser(String token);
   void addLaptopToCart(String token, Long laptopId, Integer quantity);
   void removeLaptopInCart(TokenAndIdsDTO dto);

   void reduceLaptopQuantity(TokenAndIdsDTO dto);
}
