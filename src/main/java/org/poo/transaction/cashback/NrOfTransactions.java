package org.poo.transaction.cashback;

import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;


public class NrOfTransactions implements CashbackStrategy {
    private static final int FOOD_THRESHOLD = 2;
    private static final int CLOTHES_THRESHOLD = 5;
    private static final int TECH_THRESHOLD = 10;

    /** */
    @Override
    public double calculateCashback(final BankDatabase bank,
                                    final Account account,
                                    final double amount,
                                    final double totalAmount,
                                    final String type) {
        double cashback = CashbackStrategy.calculaetCatergoryCashback(account, totalAmount, type);
        if (account.getTotalTransaction() == FOOD_THRESHOLD && !account.isFoodCashback()) {
            account.setFoodCashback(true);
        } else if (account.getTotalTransaction() == CLOTHES_THRESHOLD) {
            account.setClothesCashback(true);
        } else if (account.getTotalTransaction() == TECH_THRESHOLD) {
            account.setTechCashback(true);
        }
        return cashback;
    }

    /** */
    @Override
    public String getName() {
        return "nrOfTransactions";
    }
}
