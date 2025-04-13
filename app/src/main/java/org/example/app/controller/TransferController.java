package org.example.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.dto.TransferDTO;
import org.example.app.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{id}/transfers")
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/between-own-cards")
    public ResponseEntity<Void> transferBetweenOwnCards(
            @RequestBody @Valid TransferDTO transferDTO,
            @PathVariable Long userId) {
        transferService.transferBetweenOwnCards(userId, transferDTO);
        return ResponseEntity.ok().build();
    }
}
