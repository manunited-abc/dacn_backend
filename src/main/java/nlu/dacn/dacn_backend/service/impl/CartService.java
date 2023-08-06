package nlu.dacn.dacn_backend.service.impl;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.converter.LaptopConverter;
import nlu.dacn.dacn_backend.dto.request.CartDTO;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.dto.request.TokenAndIdsDTO;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.entity.Cart;
import nlu.dacn.dacn_backend.entity.LaptopQuantityEntry;
import nlu.dacn.dacn_backend.entity.Laptop;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.repository.LaptopRepository;
import nlu.dacn.dacn_backend.security.jwt.JwtTokenProvider;
import nlu.dacn.dacn_backend.service.ICartService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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

            List<LaptopQuantityEntry> cartLaptopEntries = cart.getCartLaptop();
            Map<Laptop, Integer> cartLaptopMap = new HashMap<>();
            List<LaptopDTO> dtoList = cartDTO.getLaptopDTOs();

            LaptopDTO laptopDTO;
            if (!cartLaptopEntries.isEmpty()) {
                List<Laptop> laptopList = new ArrayList<>();
                for (LaptopQuantityEntry entry : cartLaptopEntries) {
                    Laptop laptop = entry.getLaptop();
                    int quantity = entry.getQuantity();
                    laptopList.add(laptop);
                    cartLaptopMap.put(laptop, entry.getQuantity());

                    laptopDTO = laptopConverter.toLaptopDTO(laptop);
                    laptopDTO.setQuantity(quantity);
                    laptopDTO.setTotalAmout(laptopDTO.getPrice() * laptopDTO.getQuantity());
                    dtoList.add(laptopDTO);
                    cartDTO.setTotalPayment(cartDTO.getTotalPayment() + laptopDTO.getTotalAmout());
                }
            }
            Collections.reverse(cartDTO.getLaptopDTOs());
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
        if (laptopOptional.get().getQuantity() < quantity) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Số lượng vượt quá số lượng sản phầm hiện có");
        }

        Account account = accountOptional.get();
        Laptop laptop = laptopOptional.get();
        Cart cart = account.getCart();

        List<LaptopQuantityEntry> cartLaptopEntries = cart.getCartLaptop();
        Map<Laptop, Integer> laptopQuantityMap = new HashMap<>();
        for (LaptopQuantityEntry entry : cartLaptopEntries) {
            laptopQuantityMap.put(entry.getLaptop(), entry.getQuantity());
        }

        if (laptopQuantityMap.containsKey(laptop)) {
            for (LaptopQuantityEntry entry : cartLaptopEntries) {
                if (entry.getLaptop().equals(laptop)) {
                    entry.setQuantity(quantity + entry.getQuantity());
                    break;
                }
            }
        } else {
            LaptopQuantityEntry entry = new LaptopQuantityEntry();
            entry.setLaptop(laptop);
            entry.setQuantity(quantity);
            cartLaptopEntries.add(entry);
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
            List<LaptopQuantityEntry> cartLaptopEntries = cart.getCartLaptop();

            Optional<Laptop> optionalLaptop;
            Laptop laptop;
            Long id = ids.get(0);
            optionalLaptop = laptopRepository.findById(id);
            if (optionalLaptop.isPresent()) {
                laptop = optionalLaptop.get();
                for (LaptopQuantityEntry entry : cartLaptopEntries) {
                    if (entry.getLaptop().equals(laptop)) {
                        cartLaptopEntries.remove(entry);
                        break;
                    }
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
            List<LaptopQuantityEntry> cartLaptopEntries = cart.getCartLaptop();

            Long id = dto.getIds().get(0);
            Optional<Laptop> optional = laptopRepository.findById(id);
            Laptop laptop;
            if (optional.isPresent()) {
                laptop = optional.get();
                for (LaptopQuantityEntry entry : cartLaptopEntries) {
                    if (laptop.equals(entry.getLaptop())) {
                        int quantity = entry.getQuantity();
                        entry.setQuantity(quantity > 1 ? quantity - 1 : 1);
                    }
                }
            }
            accountRepository.save(account);
        }
    }
}
