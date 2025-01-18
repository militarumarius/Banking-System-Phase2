package org.poo.bank.plans;

import lombok.Getter;

public class SilverPlan implements Plan {
    @Override
    public double calculateFee(double transactionAmount) {
        if (transactionAmount < 500) {
            return 0;
        }
        return 0.001;
    }

    @Override
    public String getName() {
        return "silver";
    }

}