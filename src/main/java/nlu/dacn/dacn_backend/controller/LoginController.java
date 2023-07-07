package nlu.dacn.dacn_backend.controller;

import nlu.dacn.dacn_backend.dto.request.LoginForm;
import nlu.dacn.dacn_backend.dto.request.LoginGoogleRequest;
import nlu.dacn.dacn_backend.dto.response.AccessTokenResponse;
import nlu.dacn.dacn_backend.dto.response.ResponMessenger;
import nlu.dacn.dacn_backend.service.IAccountService;
import nlu.dacn.dacn_backend.service.impl.FacebookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class LoginController {
    @Autowired
    IAccountService accountService;

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    private String token_uri;

    private FacebookApiService facebookApiService;

    @PostMapping("auth/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody LoginForm loginForm) {
        return ResponseEntity.ok(accountService.login(loginForm.getUsername(), loginForm.getPassword()));
    }

    @PostMapping("auth/loginGoogle")
    public ResponseEntity<?> loginWithGoogle(@RequestBody LoginGoogleRequest request) {
        return ResponseEntity.ok(accountService.loginGoogle(request));
    }

    @GetMapping("/test")
    public String test() {
        return "Test";
    }


    @GetMapping("oauth2/authorization/facebook/v2")
    public ResponseEntity<?>  handleFacebookCallback(@RequestParam("code") String code) {
        // 1. Lưu mã code
        String authorizationCode = code;

        // 2. Gửi yêu cầu POST để trao đổi mã code và lấy token truy cập từ Facebook
        String accessToken = exchangeAuthorizationCodeForAccessToken(authorizationCode);

        facebookApiService = new FacebookApiService();
        String email = facebookApiService.getUserEmailFromFacebook(accessToken);
        String name = facebookApiService.getUserNameFromFacebook(accessToken);
        // Chuyển hướng người dùng đến trang /home
        return ResponseEntity.ok(new ResponMessenger("aaaaa"));
    }

    private String exchangeAuthorizationCodeForAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        // Định dạng URL endpoint để gửi yêu cầu trao đổi mã code
        String tokenEndpoint = token_uri;

        // Thiết lập các thông tin yêu cầu
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Gửi yêu cầu POST và nhận phản hồi
        ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                requestEntity,
                AccessTokenResponse.class
        );

        // Kiểm tra phản hồi
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            AccessTokenResponse accessTokenResponse = responseEntity.getBody();
            if (accessTokenResponse != null) {
                return accessTokenResponse.getAccessToken();
            }
        }
        return null;
    }

}
