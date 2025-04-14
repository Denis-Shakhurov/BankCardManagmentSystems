package org.example.app.dto.limit;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
public class LimitUpdateDTO {
    private final PeriodType periodType;

    @Positive
    private final BigDecimal limitAmount;

    @FutureOrPresent
    private final LocalDate periodStartDate;

    private final boolean active;
}