package com.kata.Bank.account.kata.service;

import com.kata.Bank.account.kata.domain.Operation;
import com.kata.Bank.account.kata.domain.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2026-06-21T10:00:00Z");
    private static final ZoneId FIXED_ZONE = ZoneId.of("UTC");
    private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_INSTANT, FIXED_ZONE);

    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl(FIXED_CLOCK);
    }

    @Test
    void deposit_shouldIncreaseBalanceAndRecordOperation() {
        accountService.deposit(BigDecimal.valueOf(100));

        String statement = accountService.printStatement();
        assertTrue(statement.contains("DEPOSIT"));
        assertTrue(statement.contains("100.00"));
        assertTrue(statement.contains("100.00"));
    }

    @Test
    void withdraw_shouldDecreaseBalanceAndRecordOperation() {
        accountService.deposit(BigDecimal.valueOf(200));
        accountService.withdraw(BigDecimal.valueOf(50));

        String statement = accountService.printStatement();
        assertTrue(statement.contains("200.00"));
        assertTrue(statement.contains("150.00"));
    }

    @Test
    void withdraw_withInsufficientBalance_shouldThrow() {
        accountService.deposit(BigDecimal.valueOf(100));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(BigDecimal.valueOf(200)));
        assertEquals("Insufficient balance", ex.getMessage());
    }

    @Test
    void deposit_withNegativeAmount_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(BigDecimal.valueOf(-50)));
        assertEquals("Amount must be > 0", ex.getMessage());
    }

    @Test
    void deposit_withZeroAmount_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(BigDecimal.ZERO));
        assertEquals("Amount must be > 0", ex.getMessage());
    }

    @Test
    void withdraw_withNegativeAmount_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(BigDecimal.valueOf(-10)));
        assertEquals("Amount must be > 0", ex.getMessage());
    }

    @Test
    void printStatement_withNoOperations_shouldReturnEmptyMessage() {
        String statement = accountService.printStatement();
        assertEquals("No operations yet.", statement);
    }

    @Test
    void multipleOperations_shouldMaintainCorrectBalance() {
        accountService.deposit(BigDecimal.valueOf(100));
        accountService.deposit(BigDecimal.valueOf(50));
        accountService.withdraw(BigDecimal.valueOf(30));
        accountService.deposit(BigDecimal.valueOf(10));
        accountService.withdraw(BigDecimal.valueOf(5));

        String statement = accountService.printStatement();
        assertTrue(statement.contains("125.00"));
    }

    @Test
    void deposit_shouldRecordOperationWithCorrectTimestamp() {
        accountService.deposit(BigDecimal.valueOf(100));

        String statement = accountService.printStatement();
        assertTrue(statement.contains("2026-06-21 10:00:00"));
    }

    @Test
    void withdraw_shouldRecordOperationWithCorrectType() {
        accountService.deposit(BigDecimal.valueOf(100));
        accountService.withdraw(BigDecimal.valueOf(30));

        String statement = accountService.printStatement();
        assertTrue(statement.contains("WITHDRAWAL"));
    }

    @Test
    void statement_shouldContainHeader() {
        accountService.deposit(BigDecimal.valueOf(100));

        String statement = accountService.printStatement();
        assertTrue(statement.contains("Date"));
        assertTrue(statement.contains("Type"));
        assertTrue(statement.contains("Amount"));
        assertTrue(statement.contains("Balance"));
    }

    @Test
    void concurrentDepositsAndWithdrawals_shouldMaintainCorrectBalance() throws Exception {
        int threadCount = 10;
        var latch = new CountDownLatch(threadCount);
        var executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                accountService.deposit(BigDecimal.valueOf(100));
                accountService.withdraw(BigDecimal.valueOf(50));
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        String statement = accountService.printStatement();
        assertTrue(statement.contains("500.00"), "Expected balance 500, got: " + statement);
        assertEquals(20, statement.split("\n").length - 2);
    }

    @Test
    void operations_shouldBeStoredInChronologicalOrder() {
        accountService.deposit(BigDecimal.valueOf(50));
        accountService.withdraw(BigDecimal.valueOf(10));
        accountService.deposit(BigDecimal.valueOf(20));

        String statement = accountService.printStatement();
        int deposit1 = statement.indexOf("50.00");
        int withdrawal = statement.indexOf("10.00");
        int deposit2 = statement.indexOf("20.00");
        assertTrue(deposit1 < withdrawal);
        assertTrue(withdrawal < deposit2);
    }
}
