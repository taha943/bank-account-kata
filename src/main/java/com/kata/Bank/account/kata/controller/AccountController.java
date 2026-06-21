package com.kata.Bank.account.kata.controller;

import com.kata.Bank.account.kata.dto.DepositRequest;
import com.kata.Bank.account.kata.dto.WithdrawRequest;
import com.kata.Bank.account.kata.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;



    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@Valid @RequestBody DepositRequest request) {
        accountService.deposit(request.amount());
        return ResponseEntity.ok(Map.of(
                "message", "Deposit successful",
                "amount", request.amount()
        ));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(@Valid @RequestBody WithdrawRequest request) {
        accountService.withdraw(request.amount());
        return ResponseEntity.ok(Map.of(
                "message", "Withdraw successful",
                "amount", request.amount()
        ));
    }




    @GetMapping(value = "/statement/print", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> printStatement() {
        return ResponseEntity.ok(accountService.printStatement());
    }
}
