package com.kata.Bank.account.kata.service;


import java.math.BigDecimal;

public interface AccountService {

    void deposit(BigDecimal amount);

    void withdraw(BigDecimal amount);

    String printStatement();
}
