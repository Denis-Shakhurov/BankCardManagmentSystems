package org.example.app.dto.card;

import lombok.Getter;
import lombok.Setter;
import org.example.app.model.StatusCard;

import java.time.LocalDate;

@Getter
@Setter
public class CardParamDTO {
    private StatusCard status;
    private Long userId;
    private String cardNumber;
    private String cardHolderName;
    private LocalDate createdAt;
}
