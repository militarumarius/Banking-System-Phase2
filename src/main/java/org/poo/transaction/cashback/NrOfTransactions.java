package org.poo.transaction.cashback;

import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.transaction.Transaction;

public class NrOfTransactions implements CashbackStrategy {
    private static final int FOOD_THRESHOLD = 2;
    private static final int CLOTHES_THRESHOLD = 5;
    private static final int TECH_THRESHOLD = 10;

    private static final double FOOD_CASHBACK = 0.02;
    private static final double CLOTHES_CASHBACK = 0.05;
    private static final double TECH_CASHBACK = 0.1;
    @Override
    public double calculateCashback(BankDatabase bank, Account account, double amount, double totalAmount, String type) {
//        if (type.equals("Food") && account.isFoodCashback()){
//            account.setFoodCashback(false);
//            return totalAmount * FOOD_CASHBACK;
//        } else  if(type.equals("Clothes") && account.isClothesCashback()){
//            account.setClothesCashback(false);
//            return totalAmount * CLOTHES_CASHBACK;
//        } else  if(type.equals("Tech") && account.isTechCashback()){
//            account.setTechCashback(false);
//            return totalAmount * TECH_CASHBACK;
//        }
//        if (account.getTotalTransaction() == FOOD_THRESHOLD) {
//            account.setFoodCashback(true);
//        } else if (account.getTotalTransaction() == CLOTHES_THRESHOLD){
//            account.setClothesCashback(true);
//        } else if (account.getTotalTransaction() == TECH_THRESHOLD) {
//            account.setTechCashback(true);
//        }
        return 0;
    }

    @Override
    public String getName() {
        return "nrOfTransactions";
    }
}
