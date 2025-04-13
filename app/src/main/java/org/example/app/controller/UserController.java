package org.example.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.component.JWTUtils;
import org.example.app.dto.AuthDTO;
import org.example.app.dto.user.UserCreateDTO;
import org.example.app.dto.user.UserDTO;
import org.example.app.dto.user.UserUpdateDTO;
import org.example.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final JWTUtils jwtUtils;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> show(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> userDTOList = userService.findAll();
        return ResponseEntity.ok(userDTOList);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserCreateDTO createDTO) {
        UserDTO userDTO = userService.create(createDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthDTO authDTO) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authDTO.getEmail(), authDTO.getPassword());

        authenticationManager.authenticate(authentication);
        String token = jwtUtils.generateToken(authDTO.getEmail());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO updateDTO) {
        UserDTO userDTO = userService.update(id, updateDTO);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
