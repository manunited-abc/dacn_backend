package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.dto.request.AccountDTO;
import nlu.dacn.dacn_backend.dto.response.JwtResponse;
import nlu.dacn.dacn_backend.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface IAccountService {
    JwtResponse login(String username, String password);
    AccountDTO addAccount(AccountDTO dto);
    Optional<Account> findByEmail(String email);
}
