package org.example.app.dto.limit;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.app.model.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for {@link org.example.app.model.Limit}
 */
@AllArgsConstructor
@Getter
public class LimitCreateDTO {
    private final PeriodType periodType;

    @NotNull
    @NegativeOrZero
    private final BigDecimal limitAmount;

    private final BigDecimal usedAmount;

    @PastOrPresent
    private final LocalDate periodStartDate;
}