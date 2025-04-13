package org.example.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.component.UserUtils;
import org.example.app.dto.TransferDTO;
import org.example.app.model.User;
import org.example.app.service.TransferService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/transfers")
@Tag(name = "Transfer Management", description = "API for managing money transfers between cards")
@SecurityRequirement(name = "bearerAuth") // Добавляем требование аутентификации
public class TransferController {
    private final TransferService transferService;
    private final UserUtils userUtils;

    @Operation(
            summary = "Transfer between own cards",
            description = "Transfers money between cards belonging to the same user",
            security = @SecurityRequirement(name = "bearerAuth") // Указываем, что требуется аутентификация
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid transfer data or insufficient funds",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user doesn't have access to one of the cards",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "One of the cards not found",
                    content = @Content
            )
    })
    @PostMapping(
            value = "/between-own-cards",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> transferBetweenOwnCards(
            @Parameter(
                    description = "Transfer details including source card, target card and amount",
                    required = true,
                    schema = @Schema(implementation = TransferDTO.class))
            @RequestBody @Valid TransferDTO transferDTO) {
        User user = userUtils.getCurrentUser();
        Long userId = user.getId();

        transferService.transferBetweenOwnCards(userId, transferDTO);
        return ResponseEntity.ok().build();
    }
}
