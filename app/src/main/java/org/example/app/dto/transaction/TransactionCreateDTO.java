package org.example.app.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.model.TransactionType;

import java.math.BigDecimal;

/**
 * DTO for {@link org.example.app.model.Transaction}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateDTO {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private TransactionType type;
}