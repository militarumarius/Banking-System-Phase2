package org.poo.bank.plans;

import lombok.Getter;


@Getter
public class GoldPlan implements Plan {

    @Override
    public double calculateFee(double transactionAmount) {
        return 0;
    }

    @Override
    public double getThirdCashback() {
        return 0.007;
    }

    @Override
    public double getSecondCashback() {
        return 0.0055;
    }

    @Override
    public double getFirstCashback() {
        return 0.005;
    }

    @Override
    public String getName() {
        return "gold";
    }
}