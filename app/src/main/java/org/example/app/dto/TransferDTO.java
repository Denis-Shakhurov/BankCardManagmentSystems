package org.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    @NotNull
    private Long fromCardId;

    @NotNull
    private Long toCardId;

    @Positive
    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String description;
}
