package org.poo.transaction.cashback;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;

public class SpendingThreshold implements CashbackStrategy {

    private static final int THIRD_THRESHOLD = 500;
    private static final int SECOND_THRESHOLD = 300;
    private static final int FIRST_THRESHOLD = 100;
    /** */
    @Override
    public double calculateCashback(final BankDatabase bank,
                                    final Account account,
                                    final double amount,
                                    final double totalAmount,
                                    final String type) {
        account.addAmount(amount);
        double cashback = CashbackStrategy.calculaetCatergoryCashback(account, totalAmount, type);
        User user = bank.findUserByIban(account.getIBAN());
        if (account.getTotalAmount() >= THIRD_THRESHOLD) {
            return totalAmount * user.getPlan().getThirdCashback() + cashback;
        } else if (account.getTotalAmount() >= SECOND_THRESHOLD) {
            return totalAmount * user.getPlan().getSecondCashback() + cashback;
        } else if (account.getTotalAmount() >= FIRST_THRESHOLD) {
            return totalAmount * user.getPlan().getFirstCashback() + cashback;
        }
        return 0.0 + cashback;
    }

    /** */
    @Override
    public String getName() {
        return "spendingThreshold";
    }
}
