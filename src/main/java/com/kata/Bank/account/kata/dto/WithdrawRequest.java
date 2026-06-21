package com.kata.Bank.account.kata.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawRequest(
        @Positive(message = "Amount must be > 0")
        BigDecimal amount
) {}