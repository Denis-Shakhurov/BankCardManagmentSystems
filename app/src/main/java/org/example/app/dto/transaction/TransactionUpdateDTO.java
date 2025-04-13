package org.example.app.dto.transaction;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link org.example.app.model.Transaction}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionUpdateDTO {
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String description;

    @PastOrPresent
    private LocalDateTime transactionDate;

    private TransactionType type;
}