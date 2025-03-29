package com.silentowl.banking_app.transaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DepositRequest {
    private BigDecimal amount;
}
