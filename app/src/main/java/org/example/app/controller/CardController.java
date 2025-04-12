package org.example.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.dto.card.CardCreateDTO;
import org.example.app.dto.card.CardDTO;
import org.example.app.dto.card.CardUpdateDTO;
import org.example.app.service.CardService;
import org.springframework.http.HttpStatus;
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
public class CardController {
    private final CardService cardService;

    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> show(@PathVariable Long id) {
        CardDTO cardDTO = cardService.findById(id);
        return ResponseEntity.ok(cardDTO);
    }

    @GetMapping
    public ResponseEntity<List<CardDTO>> index(@RequestParam Long userId) {
        List<CardDTO> cardDTOS = cardService.findAllByUserId(userId);
        return ResponseEntity.ok(cardDTOS);
    }

    @PostMapping
    public ResponseEntity<CardDTO> create(
            @Valid @RequestBody CardCreateDTO createDTO,
            @RequestParam Long userId) {
        CardDTO cardDTO = cardService.create(createDTO, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardDTO);
    }

    @PostMapping("/{id}")
    public ResponseEntity<CardDTO> update(
            @Valid @RequestBody CardUpdateDTO updateDTO,
            @PathVariable Long id) {
        CardDTO cardDTO = cardService.update(updateDTO, id);
        return ResponseEntity.ok(cardDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
