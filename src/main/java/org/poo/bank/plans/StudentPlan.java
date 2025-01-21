package org.poo.bank.plans;

import lombok.Getter;

@Getter
public class StudentPlan implements Plan {
    private static final double THIRD_CASHBACK_RATE = 0.0025;
    private static final double SECOND_CASHBACK_RATE = 0.002;
    private static final double FIRST_CASHBACK_RATE = 0.001;
    private static final int GOLD_FEE = 350;
    private static final int SILVER_FEE_LOWER = 100;

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
    public int getFeeUpgradeSilver() {
        return SILVER_FEE_LOWER;
    }

    /** */
    @Override
    public int getFeeUpgradeGold() {
        return GOLD_FEE;
    }

    /** */
    @Override
    public String getName() {
        return "student";
    }
}
