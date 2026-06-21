package com.kata.Bank.account.kata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.Bank.account.kata.dto.DepositRequest;
import com.kata.Bank.account.kata.dto.WithdrawRequest;
import com.kata.Bank.account.kata.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void deposit_shouldCallServiceAndReturnOk() throws Exception {
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deposit successful"))
                .andExpect(jsonPath("$.amount").value(100));

        verify(accountService).deposit(BigDecimal.valueOf(100));
    }

    @Test
    void deposit_withInvalidAmount_shouldReturnBadRequest() throws Exception {
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(-50));

        mockMvc.perform(post("/api/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).deposit(any());
    }

    @Test
    void withdraw_shouldCallServiceAndReturnOk() throws Exception {
        WithdrawRequest request = new WithdrawRequest(BigDecimal.valueOf(50));

        mockMvc.perform(post("/api/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Withdraw successful"))
                .andExpect(jsonPath("$.amount").value(50));

        verify(accountService).withdraw(BigDecimal.valueOf(50));
    }

    @Test
    void withdraw_withInvalidAmount_shouldReturnBadRequest() throws Exception {
        WithdrawRequest request = new WithdrawRequest(BigDecimal.valueOf(0));

        mockMvc.perform(post("/api/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).withdraw(any());
    }

    @Test
    void deposit_withNegativeAmount_shouldReturnBadRequest() throws Exception {
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(-1));

        mockMvc.perform(post("/api/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).deposit(any());
    }

    @Test
    void withdraw_serviceThrows_shouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Insufficient balance"))
                .when(accountService).withdraw(BigDecimal.valueOf(999));

        WithdrawRequest request = new WithdrawRequest(BigDecimal.valueOf(999));

        mockMvc.perform(post("/api/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient balance"));
    }

    @Test
    void printStatement_withOperations_shouldReturnStatementText() throws Exception {
        when(accountService.printStatement()).thenReturn("Date                 | Type         | Amount    | Balance   \n" +
                "------------------------------------------------------------\n" +
                "2026-06-21 10:00:00 | DEPOSIT      | 100.00    | 100.00    ");

        mockMvc.perform(get("/api/account/statement/print"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("DEPOSIT")));

        verify(accountService).printStatement();
    }

    @Test
    void printStatement_withNoOperations_shouldReturnNoOpsMessage() throws Exception {
        when(accountService.printStatement()).thenReturn("No operations yet.");

        mockMvc.perform(get("/api/account/statement/print"))
                .andExpect(status().isOk())
                .andExpect(content().string("No operations yet."));

        verify(accountService).printStatement();
    }
}
