package nlu.dacn.dacn_backend.controller;

import nlu.dacn.dacn_backend.dto.request.LoginForm;
import nlu.dacn.dacn_backend.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class LoginController {
    @Autowired
    IAccountService accountService;

    @PostMapping("auth/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody LoginForm loginForm) {
        return ResponseEntity.ok(accountService.login(loginForm.getUsername(), loginForm.getPassword()));
    }
}
