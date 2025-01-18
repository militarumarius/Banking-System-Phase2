package org.poo.bank.plans;

import lombok.Getter;
import lombok.Setter;

public interface Plan {
    double calculateFee(double transactionAmount);
    String getName();
}