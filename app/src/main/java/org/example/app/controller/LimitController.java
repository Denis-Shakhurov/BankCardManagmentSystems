package org.example.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.dto.limit.LimitCreateDTO;
import org.example.app.dto.limit.LimitDTO;
import org.example.app.dto.limit.LimitUpdateDTO;
import org.example.app.service.LimitService;
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
@RequiredArgsConstructor
@RequestMapping("/api/cards/{cardId}/limits")
public class LimitController {
    private final LimitService limitService;

    @GetMapping("/{id}")
    public ResponseEntity<LimitDTO> show(@PathVariable Long id) {
        LimitDTO limitDTO = limitService.findById(id);
        return ResponseEntity.ok(limitDTO);
    }

    @GetMapping
    public ResponseEntity<List<LimitDTO>> index(@PathVariable Long cardId) {
        List<LimitDTO> limitDTOS = limitService.findAll(cardId);
        return ResponseEntity.ok(limitDTOS);
    }

    @PostMapping
    public ResponseEntity<LimitDTO> create(
            @PathVariable Long cardId,
            @Valid @RequestBody LimitCreateDTO createDTO) {
        LimitDTO limitDTO = limitService.save(createDTO, cardId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(limitDTO);
    }

    @PostMapping("/{id}")
    public ResponseEntity<LimitDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody LimitUpdateDTO updateDTO) {
        LimitDTO limitDTO = limitService.update(id, updateDTO);
        return ResponseEntity.ok(limitDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        limitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
