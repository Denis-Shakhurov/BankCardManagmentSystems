package org.example.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.component.UserUtils;
import org.example.app.dto.TransferDTO;
import org.example.app.model.User;
import org.example.app.service.TransferService;
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
public class TransferController {
    private final TransferService transferService;
    private final UserUtils userUtils;

    @PostMapping("/between-own-cards")
    public ResponseEntity<Void> transferBetweenOwnCards(
            @RequestBody @Valid TransferDTO transferDTO) {
        User user = userUtils.getCurrentUser();
        Long userId = user.getId();

        transferService.transferBetweenOwnCards(userId, transferDTO);
        return ResponseEntity.ok().build();
    }
}
