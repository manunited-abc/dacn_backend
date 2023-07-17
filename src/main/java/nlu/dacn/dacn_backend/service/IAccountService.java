package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.dto.request.AccountDTO;
import nlu.dacn.dacn_backend.dto.request.LoginFacebookRequest;
import nlu.dacn.dacn_backend.dto.request.LoginGoogleRequest;
import nlu.dacn.dacn_backend.dto.response.JwtResponse;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IAccountService {
    JwtResponse login(String username, String password) ;


    void sendCodeToEmail(String host, String token);

    void processResetPassword(String token, String password);

    AccountDTO addAccount(AccountDTO dto);


    Optional<Account> findByEmail(String email);

    AccountDTO update(AccountDTO accountDTO, String token);

    void updateAccountByAdmin(AccountDTO accountDTO, String token);

    Optional<Account> findByUserName(String username);

    JwtResponse loginGoogle(LoginGoogleRequest loginGoogleRequest);

    JwtResponse loginFacebook(LoginFacebookRequest loginFacebookRequest);
}
