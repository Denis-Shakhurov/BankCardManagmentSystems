package org.example.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.component.JWTUtils;
import org.example.app.dto.AuthDTO;
import org.example.app.dto.user.UserCreateDTO;
import org.example.app.dto.user.UserDTO;
import org.example.app.dto.user.UserUpdateDTO;
import org.example.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "User Management", description = "API for user registration, authentication and management")
public class UserController {
    private final JWTUtils jwtUtils;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user details by user ID (Admin only)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - admin access required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> show(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves list of all users (Admin only)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO[].class))
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> userDTOList = userService.findAll();
        return ResponseEntity.ok(userDTOList);
    }

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content
            )
    })
    @SecurityRequirements() // No auth required
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> register(
            @Parameter(description = "User registration data", required = true)
            @RequestBody @Valid UserCreateDTO createDTO) {
        UserDTO userDTO = userService.create(createDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDTO);
    }

    @Operation(
            summary = "User login",
            description = "Authenticates user and returns JWT token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid credentials format",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content
            )
    })
    @SecurityRequirements() // No auth required
    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> login(
            @Parameter(description = "User credentials", required = true)
            @RequestBody @Valid AuthDTO authDTO) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authDTO.getEmail(), authDTO.getPassword());

        authenticationManager.authenticate(authentication);
        String token = jwtUtils.generateToken(authDTO.getEmail());
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Update user",
            description = "Updates user profile information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - can only update own profile",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "ID of the user to update", required = true)
            @PathVariable Long id,

            @Parameter(description = "Updated user data", required = true)
            @RequestBody @Valid UserUpdateDTO updateDTO) {
        UserDTO userDTO = userService.update(id, updateDTO);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes user account (Admin only)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - admin access required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
