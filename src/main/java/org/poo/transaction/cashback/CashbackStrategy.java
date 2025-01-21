package org.poo.transaction.cashback;

import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;

public interface CashbackStrategy {
    double FOOD_CASHBACK = 0.02;
    double TECH_CASHBACK = 0.1;
    double CLOTHES_CASHBACK = 0.05;

    /** */
    double calculateCashback(BankDatabase bank,
                             Account account,
                             double amount,
                             double totalAmount,
                             String type);

    /** */
    String getName();

    /** */
    static double calculaetCatergoryCashback(Account account, double totalAmount, String type) {
        if (type.equals("Food") && account.isFoodCashback()) {
            account.setFoodCashback(false);
            return totalAmount * FOOD_CASHBACK;
        } else  if (type.equals("Clothes") && account.isClothesCashback()) {
            account.setClothesCashback(false);
            return totalAmount * CLOTHES_CASHBACK;
        } else  if (type.equals("Tech") && account.isTechCashback()) {
            account.setTechCashback(false);
            return totalAmount * TECH_CASHBACK;
        }
        return 0;
    }
}
