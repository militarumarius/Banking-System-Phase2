package org.poo.bank.accounts;

public class EconomyAccount extends Account {
    public EconomyAccount(final String iban,
                          final String type,
                          final String currency,
                          final double interestRate) {
        super(iban, type, currency);
        this.setInterestRate(interestRate);
    }

    public EconomyAccount(final EconomyAccount account) {
        super(account);
        this.setInterestRate(account.getInterestRate());
    }

}
