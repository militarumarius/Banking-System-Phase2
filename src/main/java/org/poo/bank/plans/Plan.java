package org.poo.bank.plans;

import lombok.Getter;
import lombok.Setter;

public interface Plan {
    double calculateFee(double transactionAmount);
    double getThirdCashback();
    double getSecondCashback();
    double getFirstCashback();
    String getName();
}