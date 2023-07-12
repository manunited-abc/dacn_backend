package nlu.dacn.dacn_backend.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.converter.AccountConverter;
import nlu.dacn.dacn_backend.dto.request.AccountDTO;
import nlu.dacn.dacn_backend.dto.request.LoginFacebookRequest;
import nlu.dacn.dacn_backend.dto.request.LoginGoogleRequest;
import nlu.dacn.dacn_backend.dto.response.JwtResponse;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.entity.Role;
import nlu.dacn.dacn_backend.enumv1.LoginType;
import nlu.dacn.dacn_backend.enumv1.RoleType;
import nlu.dacn.dacn_backend.enumv1.State;
import nlu.dacn.dacn_backend.evenlistener.AccountCreatedEvent;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.mail.GMailer;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.security.jwt.JwtTokenProvider;
import nlu.dacn.dacn_backend.security.useprincal.UserPrinciple;
import nlu.dacn.dacn_backend.service.IAccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

@Component
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, String> tokenLoginMap = new HashMap<>();


    private final RoleService roleService;
    private final ApplicationContext applicationContext;
    private final AccountConverter accountConverter;
    private final PasswordEncoder passwordEncoder;
//    private final OAuth2AuthorizedClientService authorizedClientService;


    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";


    @Override
    public JwtResponse login(String username, String password) {
        // Kiểm tra tài khoản có đúng trạng thái hay không
        validateAccount(username);
        // Xác thực username và password
        Authentication authenticate;
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
            Optional<Account> optional = accountRepository.findByUserName(username);
            if (optional.isPresent()) {
                Account account = optional.get();
                account.setPassword(passwordEncoder.encode(password));
                accountRepository.save(account);
            } else {
                throw new ServiceException("Không tìm thấy tài khoản");
            }
        } else {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Token không hợp lệ hoặc đã hết hiệu lực");
        }
    }

    //   Thêm mới hoặc cập nhật tài khoản
    @Override
    public AccountDTO addAccount(AccountDTO dto) {
        Account account;
        if (dto.getRoles() == null) {
            dto.setRoles(new ArrayList<>());
        }
        if (dto.getState() == null) {
            dto.setState(State.ACTIVE);
        }
        if (accountRepository.findByUserName(dto.getUserName().trim()).isPresent()) {
            throw new ServiceException(HttpStatus.FOUND, "Tên tài khoản đã tồn tại");
        }
        if(accountRepository.findByEmailAndLoginType(dto.getEmail(),dto.getLoginType()).isPresent()){
            throw new ServiceException(HttpStatus.FOUND, "Tên tài khoản đã tồn tại");
        }
//        if (findByEmail(dto.getEmail().trim()).isPresent()) {
//            throw new ServiceException(HttpStatus.FOUND, "Email đã tồn tại");
//        }
        List<Role> roles = new ArrayList<>();
        List<Role> roleList = dto.getRoles();
        if (roleList.size() > 0) {
            roleList.forEach(role -> {
                if ("ADMIN".equals(role.getCode())) {
                    Role adminRole = roleService.findByCode(RoleType.ADMIN.getCode()).orElseThrow(
                            () -> new ServiceException(HttpStatus.NOT_FOUND, "Không tìm thấy quyền " + RoleType.ADMIN.getCode())
                    );
                    roles.add(adminRole);
                } else {
                    Role userRole = roleService.findByCode(RoleType.USER.getCode()).orElseThrow(
                            () -> new ServiceException(HttpStatus.NOT_FOUND, "Không tìm thấy quyền " + RoleType.USER.getCode())
                    );
                    roles.add(userRole);
                }
            });
        } else {
            Role userRole = roleService.findByCode(RoleType.USER.getCode()).orElseThrow(
                    () -> new ServiceException(HttpStatus.NOT_FOUND, "Không tìm thấy quyền " + RoleType.USER.getCode())
            );
            roles.add(userRole);
        }

        dto.setRoles(roles);
        account = accountConverter.toAccount(dto);
        if (!dto.isNoPassword()) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        account = accountRepository.save(account);
        applicationContext.publishEvent(new AccountCreatedEvent(account));
        return accountConverter.toAccountDTO(account);
    }


    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public AccountDTO update(AccountDTO dto, String token) {
        Account account;
        String username = jwtTokenProvider.getUserNameFromToken(token.trim());
        Optional<Account> optionalAccount = accountRepository.findByUserName(username);
        if (optionalAccount.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin tài khoản: " + username);
        }
        Account oldAccount = optionalAccount.get();
        account = accountConverter.toAccount(dto, oldAccount);
        if (dto.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        accountRepository.save(account);
        return accountConverter.toAccountDTO(account);
    }

    @Override
    public void updateAccountByAdmin(AccountDTO dto, String token) {
        Account admin;
        String username = jwtTokenProvider.getUserNameFromToken(token.trim());
        Optional<Account> optionalAccount = accountRepository.findByUserName(username);
        if (optionalAccount.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin tài khoản: " + username);
        }

        admin = optionalAccount.get();
        RoleType adminRole = RoleType.ADMIN;
        boolean isAdmin = false;
        for (Role role : admin.getRoles()) {
            if (role.getCode().equals(adminRole.getCode())) {
                isAdmin = true;
                break;
            }
        }
        if (isAdmin) {
            if (dto.getId() != null) {
                Optional<Account> optionalAccount1 = accountRepository.findById(dto.getId());
                if (optionalAccount1.isPresent()) {
                    Account oldAccount = optionalAccount1.get();
                    Account accountUpdate = accountConverter.toAccount(dto, oldAccount);
                    if (dto.getPassword() != null) {
                        accountUpdate.setPassword(passwordEncoder.encode(accountUpdate.getPassword()));
                    }
                    accountRepository.save(accountUpdate);
                } else {
                    throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản, vui lòng kiểm tra lại");
                }
            } else {
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Không tìm thấy id tài khoản");
            }
        } else {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Bạn không có quyền thay đổi");
        }
    }

    @Override
    public Optional<Account> findByUserName(String token) {
        String username = jwtTokenProvider.getUserNameFromToken(token.trim());
        Optional<Account> optionalAccount = accountRepository.findByUserName(username);
        if (optionalAccount.isPresent()) {
            return optionalAccount;
        } else {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Tài khoản không tồn tại, vui lòng thử lại");
        }
    }

    @Override
    public JwtResponse loginGoogle(LoginGoogleRequest request) {
        try {
            HttpTransport transport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(clientId))
                    .build();
            // Xác thực tokenId
            GoogleIdToken idToken = verifier.verify(request.getTokenId());
            if (idToken != null) {

                // Xác thực thành công, trả về thông tin người dùng
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");


                Optional<Account> existingUser = findByEmailAndLoginType(email,LoginType.GOOGLE);
                Account loginAcount;
                if (existingUser.isPresent()) {
                    loginAcount = existingUser.get();
                } else {
                    AccountDTO accountDTO = AccountDTO.builder()
                            .fullName(name)
                            .userName(createNewUserName())
                            .email(email)
                            .noPassword(true)
                            .loginType(LoginType.GOOGLE)
                            .build();
                    addAccount(accountDTO);
                    loginAcount = accountConverter.toAccount(accountDTO);

                }
                String username = loginAcount.getUserName();
                String tokenUser = tokenLoginMap.get(username);
                if (tokenUser == null || !jwtTokenProvider.validateToken(tokenUser)) {

                    tokenUser = jwtTokenProvider.generateToken(username);
                    tokenLoginMap.put(username, tokenUser);
                }
                JwtResponse result = new JwtResponse(tokenUser, username, loginAcount.getRoles().stream()
                        .map(Role::getCode)
                        .collect(Collectors.toList()));
                System.out.println(result.getToken());
                return result;

            } else {
                throw new ServiceException(HttpStatus.NOT_FOUND, "Lỗi google google");
            }

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Optional<Account> findByEmailAndLoginType(String email, LoginType google) {
        return accountRepository.findByEmailAndLoginType(email,google);
    }

    @Override
    public JwtResponse loginFacebook(LoginFacebookRequest request) {
        String email = request.getEmail();
        String fullName = request.getFullName();
        String username;

        Optional<Account> optional = accountRepository.findByEmailAndLoginType(email, LoginType.FACEBOOK);
        Account account;
        if (optional.isEmpty()) {
            AccountDTO accountDTO = AccountDTO.builder()
                    .fullName(fullName)
                    .userName(createNewUserName())
                    .email(email)
                    .noPassword(true)
                    .loginType(LoginType.FACEBOOK)
                    .build();
            addAccount(accountDTO);
            account = accountConverter.toAccount(accountDTO);
        } else {
            account = optional.get();
        }

        username = account.getUserName();
        String tokenUser = jwtTokenProvider.generateToken(username);
        if (tokenUser == null || !jwtTokenProvider.validateToken(tokenUser)) {
            tokenUser = jwtTokenProvider.generateToken(username);
            tokenLoginMap.put(username, tokenUser);
        }
        return new JwtResponse(tokenUser, username, account.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toList()));
    }

    // Tạo username cho tài khoản google và facebook
    private String createNewUserName() {
        return "user" + (accountRepository.findMaxId() + 1);
    }
}