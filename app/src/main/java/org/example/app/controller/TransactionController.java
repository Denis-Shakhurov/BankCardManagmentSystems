package org.example.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.dto.transaction.TransactionCreateDTO;
import org.example.app.dto.transaction.TransactionDTO;
import org.example.app.dto.transaction.TransactionUpdateDTO;
import org.example.app.service.TransactionService;
import org.springframework.http.HttpStatus;
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
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> show(@PathVariable Long id) {
        TransactionDTO transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> index(@PathVariable Long cardId) {
        List<TransactionDTO> transactionDTOS = transactionService.findALLByCardId(cardId);
        return ResponseEntity.ok(transactionDTOS);
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> create(
            @PathVariable Long cardId,
            @RequestBody @Valid TransactionCreateDTO createDTO) {
        TransactionDTO transactionDTO = transactionService.save(createDTO, cardId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDTO);
    }

    @PostMapping("/{id}")
    public ResponseEntity<TransactionDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid TransactionUpdateDTO updateDTO) {
        TransactionDTO transactionDTO = transactionService.update(id, updateDTO);
        return ResponseEntity.ok(transactionDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
