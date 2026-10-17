package com.crm.backend.auth;

import com.crm.backend.common.EmailAlreadyExistsException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail());
    }

    public AuthResponse updateProfile(User current, ProfileUpdateRequest request) {
        if (!current.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        current.setName(request.getName());
        current.setEmail(request.getEmail());
        userRepository.save(current);

        // Issue a fresh token so the JWT subject stays in sync with a changed email.
        String token = jwtService.generateToken(current);
        return new AuthResponse(token, current.getName(), current.getEmail());
    }

    public void changePassword(User current, PasswordChangeRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), current.getPassword())) {
            throw new org.springframework.security.access.AccessDeniedException("Current password is incorrect");
        }

        current.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(current);
    }

    public AuthResponse googleLogin(GoogleAuthRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory()) {
            }.setAudience(Collections.singletonList(
                    System.getenv("GOOGLE_CLIENT_ID") != null ? System.getenv("GOOGLE_CLIENT_ID") : "placeholder"
            )).build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setPassword(java.util.UUID.randomUUID().toString());
                return userRepository.save(newUser);
            });

            String token = jwtService.generateToken(user);
            return new AuthResponse(token, user.getName(), user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }
}
