package org.example.app.dto.transaction;

import lombok.Getter;
import lombok.Setter;
import org.example.app.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionParamDTO {
    private TransactionType type;
    private String description;
    private Long cardId;
    private BigDecimal amount;
    private LocalDate date;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
}
