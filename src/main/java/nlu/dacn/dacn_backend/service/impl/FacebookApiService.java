package nlu.dacn.dacn_backend.service.impl;

import nlu.dacn.dacn_backend.dto.response.FacebookUserResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class FacebookApiService {

    private RestTemplate restTemplate;

    public FacebookApiService() {
        this.restTemplate = new RestTemplate();
    }

    public String getUserEmailFromFacebook(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create("https://graph.facebook.com/v12.0/me?fields=email"));

        ResponseEntity<FacebookUserResponse> responseEntity = restTemplate.exchange(requestEntity, FacebookUserResponse.class);
        FacebookUserResponse userResponse = responseEntity.getBody();

        if (userResponse != null) {
            return userResponse.getEmail();
        }

        return null;
    }

    public String getUserNameFromFacebook(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create("https://graph.facebook.com/v12.0/me?fields=name"));

        ResponseEntity<FacebookUserResponse> responseEntity = restTemplate.exchange(requestEntity, FacebookUserResponse.class);
        FacebookUserResponse userResponse = responseEntity.getBody();

        if (userResponse != null) {
            return userResponse.getName();
        }

        return null;
    }
}
