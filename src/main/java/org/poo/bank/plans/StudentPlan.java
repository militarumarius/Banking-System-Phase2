package org.poo.bank.plans;

import lombok.Getter;

@Getter
public class StudentPlan implements Plan {
    private static final double THIRD_CASHBACK_RATE = 0.0025;
    private static final double SECOND_CASHBACK_RATE = 0.002;
    private static final double FIRST_CASHBACK_RATE = 0.001;

    /** */
    @Override
    public double calculateFee(final double transactionAmount) {
        return 0;
    }

    /** */
    @Override
    public double getThirdCashback() {
        return THIRD_CASHBACK_RATE;
    }

    /** */
    @Override
    public double getSecondCashback() {
        return SECOND_CASHBACK_RATE;
    }

    /** */
    @Override
    public double getFirstCashback() {
        return FIRST_CASHBACK_RATE;
    }

    /** */
    @Override
    public String getName() {
        return "student";
    }
}
