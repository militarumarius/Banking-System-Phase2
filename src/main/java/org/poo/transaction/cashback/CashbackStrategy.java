package org.poo.transaction.cashback;

import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.transaction.Transaction;

public interface CashbackStrategy {
    double calculateCashback(BankDatabase bank, Account account, double amount);
}