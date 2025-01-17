package org.poo.bank.accounts;

public class BasicAccount extends Account {
    public BasicAccount(final String iban, final String type, final String currency) {
        super(iban, type, currency);
    }

    public BasicAccount(final BasicAccount account) {
        super(account);
    }

}
