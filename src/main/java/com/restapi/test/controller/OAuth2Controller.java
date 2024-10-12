package com.restapi.test.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.restapi.test.model.User;
import com.restapi.test.repository.UserRepository;
import com.restapi.test.service.UserService;

@RestController
public class OAuth2Controller {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @GetMapping("/login/success")
  public ResponseEntity<?> result(@AuthenticationPrincipal OAuth2User principal) {
    
    if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

    String googleId = principal.getAttribute("sub");
    String email = principal.getAttribute("email");
    String name = principal.getAttribute("name");

    Optional<User> existingUser = userRepository.findByGoogleId(googleId);

    existingUser.ifPresentOrElse(
        user -> {
            user.setEmail(email);
            user.setName(name);
            userRepository.save(user);
        },
        () -> {
            User newUser = new User();
            newUser.setGoogleId(googleId);
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRoles(new String[]{"ROLE_USER"});
            userRepository.save(newUser);
        }
    );

    String token = userService.registerOrLoginWithGoogle(email);

    return ResponseEntity.status(HttpStatus.FOUND)
          .header("Location", "http://localhost:3000/check-auth/" + token)
          .build();
  }
}
