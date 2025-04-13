package org.example.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.dto.limit.LimitCreateDTO;
import org.example.app.dto.limit.LimitDTO;
import org.example.app.dto.limit.LimitUpdateDTO;
import org.example.app.service.LimitService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards/{cardId}/limits")
@Tag(name = "Limit Management", description = "API for managing card limits")
public class LimitController {
    private final LimitService limitService;

    @Operation(
            summary = "Get limit by ID",
            description = "Retrieves a specific limit by its ID for a card"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Limit found",
                    content = @Content(schema = @Schema(implementation = LimitDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Limit not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<LimitDTO> show(
            @Parameter(description = "ID of the card", required = true)
            @PathVariable Long cardId,

            @Parameter(description = "ID of the limit to retrieve", required = true)
            @PathVariable Long id) {
        LimitDTO limitDTO = limitService.findById(id);
        return ResponseEntity.ok(limitDTO);
    }

    @Operation(
            summary = "Get all limits for card",
            description = "Retrieves all limits associated with a specific card"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of limits retrieved successfully",
            content = @Content(schema = @Schema(implementation = LimitDTO[].class))
    )
    @GetMapping
    public ResponseEntity<List<LimitDTO>> index(
            @Parameter(description = "ID of the card to get limits for", required = true)
            @PathVariable Long cardId) {
        List<LimitDTO> limitDTOS = limitService.findAll(cardId);
        return ResponseEntity.ok(limitDTOS);
    }

    @Operation(
            summary = "Create a new limit for card",
            description = "Creates a new spending limit for a specific card"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Limit created successfully",
                    content = @Content(schema = @Schema(implementation = LimitDTO.class))
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LimitDTO> create(
            @Parameter(description = "ID of the card to add limit to", required = true)
            @PathVariable Long cardId,

            @Parameter(description = "Limit data to create", required = true)
            @Valid @RequestBody LimitCreateDTO createDTO) {
        LimitDTO limitDTO = limitService.save(createDTO, cardId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(limitDTO);
    }

    @Operation(
            summary = "Update a limit",
            description = "Updates an existing spending limit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Limit updated successfully",
                    content = @Content(schema = @Schema(implementation = LimitDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Limit not found",
                    content = @Content
            )
    })
    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LimitDTO> update(
            @Parameter(description = "ID of the limit to update", required = true)
            @PathVariable Long id,

            @Parameter(description = "Updated limit data", required = true)
            @Valid @RequestBody LimitUpdateDTO updateDTO) {
        LimitDTO limitDTO = limitService.update(id, updateDTO);
        return ResponseEntity.ok(limitDTO);
    }

    @Operation(
            summary = "Delete a limit",
            description = "Deletes a specific spending limit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Limit deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Limit not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the limit to delete", required = true)
            @PathVariable Long id) {
        limitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
