package nlu.dacn.dacn_backend.controller;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.dto.request.AccountDTO;
import nlu.dacn.dacn_backend.dto.response.ResponMessenger;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.service.impl.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.security.auth.login.AccountException;
import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/account/register")
    public ResponseEntity<?> register(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            accountService.addAccount(accountDTO);
            return new ResponseEntity<>(new ResponMessenger("Tạo tài khoản thành công"), HttpStatus.OK);
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
