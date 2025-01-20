package org.poo.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.errorprone.annotations.Keep;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommerciantInput;
import org.poo.transaction.cashback.CashbackStrategy;
import org.poo.transaction.cashback.NrOfTransactions;
import org.poo.transaction.cashback.SpendingThreshold;

@Getter
@Setter
public class Commerciant {
    private String commerciant;
    private double total;
    @JsonIgnore
    private String type;
    @JsonIgnore
    private int id;
    @JsonIgnore
    private CashbackStrategy cashbackStrategy;
    @JsonIgnore
    private String account;

    public Commerciant(final String commerciant, final double total) {
        this.commerciant = commerciant;
        this.total = total;
    }

    public Commerciant(final CommerciantInput commerciantInput) {
        this.commerciant = commerciantInput.getCommerciant();
        this.id = commerciantInput.getId();
        this.account = commerciantInput.getAccount();
        this.type = commerciantInput.getType();
        if (commerciantInput.getCashbackStrategy().equals("nrOfTransactions"))
            cashbackStrategy = new NrOfTransactions();
        else
            cashbackStrategy = new SpendingThreshold();
    }
}
