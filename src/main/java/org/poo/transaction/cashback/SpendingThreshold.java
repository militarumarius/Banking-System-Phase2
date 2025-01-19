package org.poo.transaction.cashback;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;

public class SpendingThreshold implements CashbackStrategy{
    @Override
    public double calculateCashback(BankDatabase bank, Account account, double amount, double totalAmount, String type) {
        double cashbackRate;
        account.addAmount(amount);
        User user = bank.findUserByIban(account.getIBAN());
        String userPlan = user.getPlan().getName();
        if (account.getTotalAmount() >= 500) {
            cashbackRate = switch (userPlan) {
                case "standard", "student" -> 0.0025;
                case "silver" -> 0.005;
                case "gold" -> 0.007;
                default -> 0.0;
            };
            return cashbackRate * totalAmount;
        } else if (account.getTotalAmount() >= 300) {
            cashbackRate = switch (userPlan) {
                case "standard", "student" -> 0.002;
                case "silver" -> 0.004;
                case "gold" -> 0.0055;
                default -> 0.0;
            };
            return cashbackRate * totalAmount;
        } else if (account.getTotalAmount() >= 100) {
            cashbackRate = switch (userPlan) {
                case "standard", "student" -> 0.001;
                case "silver" -> 0.003;
                case "gold" -> 0.005;
                default -> 0.0;
            };
            return cashbackRate * totalAmount;
        }
        return 0.0;
    }
    @Override
    public String getName() {
        return "spendingThreshold";
    }
}
