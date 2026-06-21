package com.kata.Bank.account.kata.service;

import com.kata.Bank.account.kata.domain.Operation;
import com.kata.Bank.account.kata.domain.OperationType;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private BigDecimal balance = BigDecimal.ZERO;
    private final List<Operation> operations = new ArrayList<>();
    private final Clock clock;

    public AccountServiceImpl() {
        this.clock = Clock.systemDefaultZone();
    }

    public AccountServiceImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void deposit(BigDecimal amount) {
        validateAmount(amount);

        balance = balance.add(amount);
        operations.add(new Operation(
                OperationType.DEPOSIT,
                amount,
                LocalDateTime.now(clock),
                balance
        ));
        log.info("Deposited {} -> new balance: {}", amount, balance);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        validateAmount(amount);

        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        balance = balance.subtract(amount);
        operations.add(new Operation(
                OperationType.WITHDRAWAL,
                amount,
                LocalDateTime.now(clock),
                balance
        ));
        log.info("Withdrew {} -> new balance: {}", amount, balance);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }
    }


    @Override
    public String printStatement() {
        if (operations.isEmpty()) {
            return "No operations yet.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s | %-12s | %-10s | %-10s%n", "Date", "Type", "Amount", "Balance"));
        sb.append("-".repeat(60)).append("\n");

        for (Operation op : operations) {
            sb.append(String.format("%-20s | %-12s | %-10s | %-10s%n",
                    op.getDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    op.getType().name(),
                    op.getAmount().setScale(2),
                    op.getBalanceAfter().setScale(2)));
        }

        return sb.toString();
    }
}
