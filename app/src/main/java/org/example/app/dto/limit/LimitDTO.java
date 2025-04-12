package org.example.app.dto.limit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.model.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for {@link org.example.app.model.Limit}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LimitDTO {
    private Long id;
    private PeriodType periodType;
    private BigDecimal limitAmount;
    private LocalDate periodStartDate;
    private boolean active;
    private Long cardId;
}