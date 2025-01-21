package org.poo.bank.plans;

public interface Plan {
    /** */
    double calculateFee(double transactionAmount);

    /** */
    double getThirdCashback();

    /** */
    double getSecondCashback();

    /** */
    double getFirstCashback();

    /** */
    int getFeeUpgradeSilver();

    /** */
    int  getFeeUpgradeGold();

    /** */
    String getName();
}
