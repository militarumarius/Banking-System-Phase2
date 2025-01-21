package org.poo.bank.plans;

import lombok.Getter;

@Getter
public class SilverPlan implements Plan {

    private static final double THIRD_CASHBACK_RATE = 0.005;
    private static final double SECOND_CASHBACK_RATE = 0.004;
    private static final double FIRST_CASHBACK_RATE = 0.003;
    private static final double FEE = 0.001;
    private static final double AMOUNT = 500;
    private static final int SILVER_FEE = 250;

    /** */
    @Override
    public double calculateFee(final double transactionAmount) {
        if (transactionAmount < AMOUNT) {
            return 0;
        }
        return FEE;
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
        return -1;
    }

    /** */
    @Override
    public int getFeeUpgradeGold() {
        return SILVER_FEE;
    }

    /** */
    @Override
    public String getName() {
        return "silver";
    }
}
