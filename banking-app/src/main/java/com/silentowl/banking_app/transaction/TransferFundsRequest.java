package com.silentowl.banking_app.transaction;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferFundsRequest {

    private BigDecimal amount;
}
