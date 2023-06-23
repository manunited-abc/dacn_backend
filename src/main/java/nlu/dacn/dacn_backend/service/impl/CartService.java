package nlu.dacn.dacn_backend.service.impl;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.converter.LaptopConverter;
import nlu.dacn.dacn_backend.dto.request.CartDTO;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.dto.request.TokenAndIdsDTO;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.entity.Cart;
import nlu.dacn.dacn_backend.entity.Laptop;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.repository.LaptopRepository;
import nlu.dacn.dacn_backend.security.jwt.JwtTokenProvider;
import nlu.dacn.dacn_backend.service.ICartService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final LaptopRepository laptopRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final LaptopConverter laptopConverter;
    private final AccountRepository accountRepository;

    @Override
    public CartDTO findLaptopByUser(String token) {
        CartDTO cartDTO = new CartDTO();
        String username = jwtTokenProvider.getUserNameFromToken(token);
        Optional<Account> accountOptional = accountRepository.findByUserName(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Cart cart = account.getCart();
            Map<Laptop, Integer> cartLaptop = cart.getCartLaptop();
            List<LaptopDTO> dtoList = cartDTO.getLaptopDTOs();

            LaptopDTO laptopDTO;
            if (!cartLaptop.isEmpty()) {
                List<Laptop> laptopList = new ArrayList<>(cartLaptop.keySet());
                for (Laptop laptop : laptopList) {
                    laptopDTO = laptopConverter.toLaptopDTO(laptop);
                    laptopDTO.setQuantity(cartLaptop.get(laptop));
                    laptopDTO.setTotalAmout(laptopDTO.getPrice() * laptopDTO.getQuantity());
                    dtoList.add(laptopDTO);
                    cartDTO.setTotalPayment(cartDTO.getTotalPayment() + laptopDTO.getTotalAmout());
                }
            }
            return cartDTO;
        }
        return new CartDTO();
    }

    @Override
    @Transactional
    public void addLaptopToCart(String token, Long laptopId, Integer quantity) {
        String username = jwtTokenProvider.getUserNameFromToken(token);
        Optional<Account> accountOptional = accountRepository.findByUserName(username);
        Optional<Laptop> laptopOptional = laptopRepository.findById(laptopId);
        if (accountOptional.isEmpty()) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Tài khoản không tồn tại, vui lòng kiểm tra lại");
        }
        if (laptopOptional.isEmpty()) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Sản phẩm không tồn tại, vui lòng kiểm tra lại");
        }
        if (laptopOptional.get().getQuantity() < 1) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Sản phẩm đã hết hàng");
        }

        Account account = accountOptional.get();
        Laptop laptop = laptopOptional.get();
        Cart cart = account.getCart();

        Map<Laptop, Integer> cartLaptop = cart.getCartLaptop();
        if (cartLaptop.containsKey(laptop)) {
            cartLaptop.put(laptop, quantity + cartLaptop.get(laptop));
        } else {
            cartLaptop.put(laptop, quantity);
        }
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void removeLaptopInCart(TokenAndIdsDTO dto) {
        String token = dto.getToken();
        List<Long> ids = dto.getIds();
        String username = jwtTokenProvider.getUserNameFromToken(token);
        Optional<Account> accountOptional = accountRepository.findByUserName(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Cart cart = account.getCart();
            Map<Laptop, Integer> cartLaptop = cart.getCartLaptop();

            Optional<Laptop> optionalLaptop;
            Laptop laptop;
            for (Long id : ids) {
                optionalLaptop = laptopRepository.findById(id);
                if (optionalLaptop.isPresent()) {
                    laptop = optionalLaptop.get();
                    cartLaptop.remove(laptop);
                }
            }
            accountRepository.save(account);
        }
    }

    @Override
    public void reduceLaptopQuantity(TokenAndIdsDTO dto) {
        String token = dto.getToken();
        String username = jwtTokenProvider.getUserNameFromToken(token);
        Optional<Account> accountOptional = accountRepository.findByUserName(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Cart cart = account.getCart();
            Map<Laptop, Integer> cartLaptop = cart.getCartLaptop();

            Optional<Laptop> optionalLaptop;
            Laptop laptop;
            int quantity;
            for (Long id : dto.getIds()) {
                optionalLaptop = laptopRepository.findById(id);
                if (optionalLaptop.isPresent()) {
                    laptop = optionalLaptop.get();
                    if (cartLaptop.containsKey(laptop)) {
                        quantity = cartLaptop.get(laptop);
                        cartLaptop.put(laptop, quantity > 1 ? quantity - 1 : 1);
                    }
                }
            }
            accountRepository.save(account);
        }
    }
}
