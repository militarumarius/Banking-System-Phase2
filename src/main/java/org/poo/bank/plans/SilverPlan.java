package org.poo.bank.plans;

import lombok.Getter;

@Getter
public class SilverPlan implements Plan {

    @Override
    public double calculateFee(double transactionAmount) {
        if (transactionAmount < 500) {
            return 0;
        }
        return 0.001;
    }

    @Override
    public double getThirdCashback() {
        return 0.005;
    }

    @Override
    public double getSecondCashback() {
        return 0.004;
    }

    @Override
    public double getFirstCashback() {
        return 0.003;
    }

    @Override
    public String getName() {
        return "silver";
    }

}