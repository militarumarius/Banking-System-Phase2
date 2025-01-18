package org.poo.bank.plans;

public class StudentPlan implements Plan {
    @Override
    public double calculateFee(double transactionAmount) {
        return 0;
    }

    @Override
    public String getName() {
        return "student";
    }
}