package com.kata.Bank.account.kata.dto;

import java.util.List;

public record StatementResponse(
        List<OperationResponse> operations
) {}