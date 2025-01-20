package org.poo.bank.plans;

import lombok.Getter;

@Getter
public class StudentPlan implements Plan {


    @Override
    public double calculateFee(double transactionAmount) {
        return 0;
    }

    @Override
    public double getThirdCashback() {
        return 0.0025;
    }

    @Override
    public double getSecondCashback() {
        return 0.002;
    }

    @Override
    public double getFirstCashback() {
        return 0.001;
    }

    @Override
    public String getName() {
        return "student";
    }
}