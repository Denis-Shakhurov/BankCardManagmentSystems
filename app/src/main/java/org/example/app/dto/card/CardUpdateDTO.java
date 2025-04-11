package org.example.app.dto.card;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.model.StatusCard;

/**
 * DTO for {@link org.example.app.model.Card}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardUpdateDTO {
    @NotNull
    private StatusCard status;
}