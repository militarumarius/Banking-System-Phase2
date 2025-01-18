package org.poo.bank.plans;

import lombok.Getter;

public class StandardPlan implements Plan {
    @Override
    public double calculateFee(double transactionAmount) {
        return 0.002;
    }

    @Override
    public String getName() {
        return "standard";
    }
}