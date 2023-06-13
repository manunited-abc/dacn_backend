package nlu.dacn.dacn_backend.service.impl;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.dto.response.JwtResponse;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.enumv1.State;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.mail.GMailer;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.security.jwt.JwtTokenProvider;
import nlu.dacn.dacn_backend.security.useprincal.UserPrinciple;
import nlu.dacn.dacn_backend.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, String> tokenLoginMap = new HashMap<>();
    private final PasswordEncoder passwordEncoder;


    @Override
    public JwtResponse login(String username, String password) {
        // Kiểm tra tài khoản có đúng trạng thái hay không
        validateAccount(username);
        // Xác thực username và password
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "Sai mật khẩu, vui lòng thử lại");
        }
        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context

        String tokenUser = tokenLoginMap.get(username);
        if (tokenUser == null || !jwtTokenProvider.validateToken(tokenUser)) {
            tokenUser = jwtTokenProvider.generateToken(authenticate);
            tokenLoginMap.put(username, tokenUser);
        }
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        UserPrinciple userPrinciple = (UserPrinciple) authenticate.getPrincipal();
        return new JwtResponse(tokenUser, userPrinciple.getUsername(), userPrinciple.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
    }

    private void validateAccount(String username) {
        Optional<Account> optionalAccount = accountRepository.findByUserName(username);
        if (optionalAccount.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản");
        }
        Account account = optionalAccount.get();
        if (account.getState() == State.PENDING) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Tài khoản chưa được kích hoạt");
        }
        if (account.getState() == State.DISABLED) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Tài khoản đã bị khóa");
        }
        if (account.getState() == State.REMOVED) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Tài khoản đã bị xóa");
        }
    }
    @Override
    public void sendCodeToEmail(String host, String username) {
        Optional<Account> accountOptional = accountRepository.findByUserName(username.trim());
        if (accountOptional.isEmpty()) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Tài khoản không tồn tại");
        }
        Account account = accountOptional.get();

        String token = jwtTokenProvider.generateToken(account.getUserName());
        account.setResetToken(token);
        accountRepository.save(account);

        String email = account.getEmail().trim();
        String resetLink = host + "?token=" + token;


        String subject = "Xác thực thay đổi mật khẩu";
        String body = "Để thay đổi mật khẩu, vui lòng nhấp vào link phía dưới để xác thực(Chỉ có hiệu lực 2 phút): \r\n" +
                resetLink;
        try {
            new GMailer().sendMail(email, subject, body);
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra, không thể gửi mail");
        }
    }

    @Override
    public void processResetPassword(String token, String password) {
        if (jwtTokenProvider.validateToken(token)) {
            password = password.trim();
            String username = jwtTokenProvider.getUserNameFromToken(token);
            Account account = accountRepository.findByUserName(username).get();
            account.setPassword(passwordEncoder.encode(password));
            accountRepository.save(account);
        } else {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Token không hợp lệ hoặc đã hết hiệu lực");
        }
    }
}
