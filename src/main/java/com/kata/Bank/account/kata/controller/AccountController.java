package com.kata.Bank.account.kata.controller;

import com.kata.Bank.account.kata.dto.DepositRequest;
import com.kata.Bank.account.kata.dto.WithdrawRequest;
import com.kata.Bank.account.kata.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;



    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@Valid @RequestBody DepositRequest request) {
        accountService.deposit(request.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@Valid @RequestBody WithdrawRequest request) {
        accountService.withdraw(request.amount());
        return ResponseEntity.ok().build();
    }




    @GetMapping(value = "/statement/print", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> printStatement() {
        return ResponseEntity.ok(accountService.printStatement());
    }
}
