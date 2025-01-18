package org.poo.bank.plans;

public class GoldPlan implements Plan {
    @Override
    public double calculateFee(double transactionAmount) {
        return 0;
    }

    @Override
    public String getName() {
        return "gold";
    }
}