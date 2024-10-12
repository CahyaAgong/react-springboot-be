package com.restapi.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.restapi.test.dto.MemberDTO;
import com.restapi.test.dto.UserDTO;
import com.restapi.test.model.User;
import com.restapi.test.repository.UserRepository;
import com.restapi.test.responses.UserResponse;
import com.restapi.test.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtUtil jwtUtil;

    public String loginUser(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) throw new Exception("Invalid Credentials");

        // return jwtUtil.generateToken(user.getEmail());
        return signInToken(user.getEmail());
    }

    public UserResponse register(UserDTO userDTO) throws Exception {
        User user = createUser(userDTO);

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setName(user.getName());
        memberDTO.setPosition("New Member");
        memberDTO.setReportsTo(null);
        memberDTO.setPictureUrl("");
        memberDTO.setUserId(user.getId());

        memberService.createMember(memberDTO);

        String token = jwtUtil.generateToken(user.getEmail());

        UserResponse response = new UserResponse();
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setToken(token);

        return response;
    }

    public String registerOrLoginWithGoogle(String data) {
        // Optional<User> existingUser = userRepository.findByGoogleId(userDTO.getGoogleId());

        // if (existingUser.isPresent()) return existingUser.get(); // User already exists, log them in

        // User user = new User();
        // user.setEmail(userDTO.getEmail());
        // user.setName(userDTO.getName());
        // user.setGoogleId(userDTO.getGoogleId());
        // user.setRoles(new String[]{"ROLE_USER"});
        // user.setCreatedAt(LocalDateTime.now());
        // return userRepository.save(user);

        return signInToken(data);
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    private User createUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(new String[]{"ROLE_USER"});
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private String signInToken(String subject) {
        return jwtUtil.generateToken(subject);
    }
}
