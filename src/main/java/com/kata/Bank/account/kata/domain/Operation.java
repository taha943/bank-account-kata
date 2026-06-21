package com.kata.Bank.account.kata.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Operation {

    private OperationType type;
    private BigDecimal amount;
    private LocalDateTime date;
    private BigDecimal  balanceAfter;
}