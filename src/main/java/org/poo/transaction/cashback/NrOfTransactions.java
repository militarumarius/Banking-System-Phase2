package org.poo.transaction.cashback;

import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.transaction.Transaction;

public class NrOfTransactions implements CashbackStrategy {
    @Override
    public double calculateCashback(BankDatabase bank, Account account, double amount, double totalAmount, String type) {
//        if (account.getTotalTransaction() == 2) {
//            account.setFoodCashback(0.02);
//        } else if (account.getTotalTransaction() == 5){
//            account.setClothesCashback(0.05);
//        } else if (account.getTotalTransaction() == 10) {
//            account.setTechCashback(0.1);
//        }
        return 0;
    }

    @Override
    public String getName() {
        return "nrOfTransactions";
    }
}
