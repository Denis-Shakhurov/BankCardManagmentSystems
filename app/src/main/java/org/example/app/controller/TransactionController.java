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
import org.example.app.dto.transaction.TransactionCreateDTO;
import org.example.app.dto.transaction.TransactionDTO;
import org.example.app.dto.transaction.TransactionParamDTO;
import org.example.app.dto.transaction.TransactionUpdateDTO;
import org.example.app.service.TransactionService;
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
@RequestMapping("/api/cards/{cardId}/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "API for managing card transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a specific transaction by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction found",
                    content = @Content(schema = @Schema(implementation = TransactionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> show(
            @Parameter(description = "ID of the card", required = true)
            @PathVariable Long cardId,

            @Parameter(description = "ID of the transaction to retrieve", required = true)
            @PathVariable Long id) {
        TransactionDTO transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @Operation(
            summary = "Get all transactions for card",
            description = "Retrieves all transactions for a specific card with optional filtering"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of transactions retrieved successfully",
            content = @Content(schema = @Schema(implementation = TransactionDTO[].class)),
            headers = @Header(name = "X-Total-Count", description = "Total count of transactions")
    )
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> findAll(
            @Parameter(description = "ID of the card to get transactions for", required = true)
            @PathVariable Long cardId,

            @Parameter(description = "Filtering parameters for transactions")
            TransactionParamDTO params) {
        params.setCardId(cardId);
        List<TransactionDTO> transactionDTOS = transactionService.findAll(params);
        return ResponseEntity.ok()
                .header("X-total-count", String.valueOf(transactionDTOS.size()))
                .body(transactionDTOS);
    }

    @Operation(
            summary = "Create a new transaction",
            description = "Creates a new transaction for a specific card"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = @Content(schema = @Schema(implementation = TransactionDTO.class))
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
    public ResponseEntity<TransactionDTO> create(
            @Parameter(description = "ID of the card to create transaction for", required = true)
            @PathVariable Long cardId,

            @Parameter(description = "Transaction data to create", required = true)
            @RequestBody @Valid TransactionCreateDTO createDTO) {
        TransactionDTO transactionDTO = transactionService.save(createDTO, cardId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDTO);
    }

    @Operation(
            summary = "Update a transaction",
            description = "Updates an existing transaction"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction updated successfully",
                    content = @Content(schema = @Schema(implementation = TransactionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content
            )
    })
    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> update(
            @Parameter(description = "ID of the transaction to update", required = true)
            @PathVariable Long id,

            @Parameter(description = "Updated transaction data", required = true)
            @RequestBody @Valid TransactionUpdateDTO updateDTO) {
        TransactionDTO transactionDTO = transactionService.update(id, updateDTO);
        return ResponseEntity.ok(transactionDTO);
    }

    @Operation(
            summary = "Delete a transaction",
            description = "Deletes a specific transaction"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Transaction deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the transaction to delete", required = true)
            @PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
