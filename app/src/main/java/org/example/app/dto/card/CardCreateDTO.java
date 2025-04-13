package org.example.app.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.model.StatusCard;

import java.math.BigDecimal;

/**
 * DTO for {@link org.example.app.model.Card}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardCreateDTO {
    @NotNull
    @NotBlank
    private String cardNumber;

    @NotNull
    @NotBlank
    private String cardHolderName;

    @NotNull
    @NotBlank
    private String expiryDate;

    @NotNull
    private StatusCard status;

    @Positive
    private BigDecimal balance;
}