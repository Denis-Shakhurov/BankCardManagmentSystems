package org.example.app.dto.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.model.StatusCard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link org.example.app.model.Card}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {
    private Long id;

    private String cardNumber;

    private String cardHolderName;

    private String expiryDate;

    private StatusCard status;

    private BigDecimal balance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Long> transactionIds;

    private List<Long> limitIds;

    private Long userId;
}