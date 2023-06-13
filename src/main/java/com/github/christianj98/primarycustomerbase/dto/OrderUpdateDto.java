package com.github.christianj98.primarycustomerbase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderUpdateDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // example: 2000-10-31T01:30:00.000-05:00
    @NotNull
    private LocalDateTime date;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
