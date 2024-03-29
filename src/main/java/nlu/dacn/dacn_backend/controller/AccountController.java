package nlu.dacn.dacn_backend.controller;

import lombok.RequiredArgsConstructor;

import nlu.dacn.dacn_backend.dto.response.ResponMessenger;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.service.impl.AccountService;
import nlu.dacn.dacn_backend.utils.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nlu.dacn.dacn_backend.dto.request.AccountDTO;


import nlu.dacn.dacn_backend.exception.ServiceException;


import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PutMapping("/account/update")
    public AccountDTO updateAccount(@RequestBody AccountDTO accountDTO, @RequestHeader("Authorization") String authHeader) {
        String token = TokenUtils.getTokenFromHeader(authHeader);
        return accountService.update(accountDTO, token);
    }

    @PutMapping("/account/admin/update")
    public ResponseEntity<?> updateAccountByAdmin(@RequestBody AccountDTO accountDTO, @RequestHeader("Authorization") String authHeader) {
        String token = TokenUtils.getTokenFromHeader(authHeader);
        accountService.updateAccountByAdmin(accountDTO, token);
        return new ResponseEntity<>(new ResponMessenger("Cập nhật tài khoản thành công"), HttpStatus.OK);
    }

    @PostMapping("/account/changePassword")
    public ResponseEntity<?> changePassword(@RequestParam String host, @RequestParam String username) {
        accountService.sendCodeToEmail(host, username);
        return new ResponseEntity<>(new ResponMessenger("Đã gửi mã xác thực qua email của bạn, vui lòng kiểm tra email"), HttpStatus.OK);
    }

    @PostMapping("/account/reset-password")
    public ResponseEntity<?> processResetPassword(@RequestParam("password") String password, @RequestParam("token") String token) {
        accountService.processResetPassword(token, password);
        return new ResponseEntity<>(new ResponMessenger("Thay đổi mật khẩu thành công"), HttpStatus.OK);
    }

    @PostMapping("/account/register")
    public ResponseEntity<?> register(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            accountService.addAccount(accountDTO);
            return new ResponseEntity<>(new ResponMessenger("Tạo tài khoản thành công"), HttpStatus.OK);
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/account/detail")
    public Account getAccount(@RequestHeader("Authorization") String authHeader) {
        String token = TokenUtils.getTokenFromHeader(authHeader);
        return accountService.findByUserName(token).get();
    }
    @GetMapping("/account/list")
    public List<Account> getAccounts() {
        return accountService.findAllAccount();
    }


}
