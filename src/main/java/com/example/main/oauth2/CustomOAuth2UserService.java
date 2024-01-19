package com.example.main.oauth2;

import com.example.main.user.SiteUser;
import com.example.main.user.UserService;
import jakarta.servlet.http.HttpSession;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final HttpSession httpSession;

    @Autowired
    public CustomOAuth2UserService(UserService userService, HttpSession httpSession) {
        this.userService = userService;
        this.httpSession = httpSession;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

//        String userNameAttributeName = userRequest.getClientRegistration()
//                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        SiteUser user;
        try {
            user = this.login(registrationId, attributes);
        } catch (ExecutionControl.NotImplementedException e) {
            throw new RuntimeException(e);
        }

        httpSession.setAttribute("user", new SessionUser(user));
        return new CustomOAuth2User(user.getUsername(), user.getEmail());
    }
    private SiteUser login(String registrationId, Map<String, Object> attributes) throws ExecutionControl.NotImplementedException {
        if (registrationId.startsWith("google")) {
            String name = (String) attributes.get("name");
            String email = (String) attributes.get("email");

            return this.userService.socialLogin(registrationId, name, email);
        } else if(registrationId.startsWith("naver")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            String name = (String) response.get("name");
            String email = (String) response.get("email");
            System.out.println("email = " + email);
            return this.userService.socialLogin(registrationId, name, email);
        }
        throw new ExecutionControl.NotImplementedException("Implemented Google and Naver login only");
    }

    class CustomOAuth2User extends SiteUser implements OAuth2User {
        public CustomOAuth2User(String username, String email) {
            super(username, email);
        }
        @Override
        public Map<String, Object> getAttributes() {
            return null;
        }

        @Override
        public String getName() {
            return this.getUsername();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return authorities;
        }
    }
}
