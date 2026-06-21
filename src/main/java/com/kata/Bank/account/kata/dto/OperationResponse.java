package com.kata.Bank.account.kata.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperationResponse(
        LocalDateTime date,
        String type,
        BigDecimal amount,
        BigDecimal  balance
) {}