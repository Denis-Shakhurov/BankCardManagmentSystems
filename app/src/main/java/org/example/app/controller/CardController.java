package org.example.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.dto.card.CardCreateDTO;
import org.example.app.dto.card.CardDTO;
import org.example.app.dto.card.CardParamDTO;
import org.example.app.dto.card.CardUpdateDTO;
import org.example.app.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "API for managing cards")
public class CardController {
    private final CardService cardService;

    @Operation(
            summary = "Get card by ID",
            description = "Retrieves a card by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card found",
                    content = @Content(schema = @Schema(implementation = CardDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> show(
            @Parameter(description = "ID of the card to be retrieved", required = true)
            @PathVariable Long id) {
        CardDTO cardDTO = cardService.findById(id);
        return ResponseEntity.ok(cardDTO);
    }

    @Operation(
            summary = "Get all cards",
            description = "Retrieves all cards with optional filtering parameters"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of cards retrieved successfully",
            content = @Content(schema = @Schema(implementation = CardDTO[].class)),
            headers = @Header(name = "X-Total-Count", description = "Total count of items")
    )
    @GetMapping
    public ResponseEntity<List<CardDTO>> showAll(
            @Parameter(description = "Filtering parameters for cards")
            CardParamDTO params) {
        List<CardDTO> cardDTOS = cardService.findAll(params);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(cardDTOS.size()))
                .body(cardDTOS);
    }

    @Operation(
            summary = "Create a new card",
            description = "Creates a new card for the specified user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Card created successfully",
                    content = @Content(schema = @Schema(implementation = CardDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CardDTO> create(
            @Parameter(description = "Card data to create", required = true)
            @Valid @RequestBody CardCreateDTO createDTO,

            @Parameter(description = "ID of the user who owns the card", required = true)
            @RequestParam Long userId) {
        CardDTO cardDTO = cardService.create(createDTO, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardDTO);
    }

    @Operation(
            summary = "Update a card",
            description = "Updates an existing card by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card updated successfully",
                    content = @Content(schema = @Schema(implementation = CardDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content
            )
    })
    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CardDTO> update(
            @Parameter(description = "Card data to update", required = true)
            @Valid @RequestBody CardUpdateDTO updateDTO,

            @Parameter(description = "ID of the card to be updated", required = true)
            @PathVariable Long id) {
        CardDTO cardDTO = cardService.update(updateDTO, id);
        return ResponseEntity.ok(cardDTO);
    }

    @Operation(
            summary = "Delete a card",
            description = "Deletes a card by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Card deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the card to be deleted", required = true)
            @PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
