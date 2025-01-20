package org.poo.transaction.cashback;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;

public class SpendingThreshold implements CashbackStrategy {
    @Override
    public double calculateCashback(BankDatabase bank, Account account, double amount, double totalAmount, String type) {
        account.addAmount(amount);
        User user = bank.findUserByIban(account.getIBAN());
        if (account.getTotalAmount() >= 500) {
            return totalAmount * user.getPlan().getThirdCashback();
        } else if (account.getTotalAmount() >= 300) {
            return totalAmount * user.getPlan().getSecondCashback();
        } else if (account.getTotalAmount() >= 100) {
            return totalAmount * user.getPlan().getFirstCashback();
        }
        return 0.0;
    }

    @Override
    public String getName() {
        return "spendingThreshold";
    }
}
