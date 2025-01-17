package org.poo.bank.cards;

import org.poo.bank.accounts.Account;

public class DebitCard extends Card {
    public DebitCard(final String cardNumber,
                     final String type,
                     final Account account) {
        super(cardNumber, type, account);
    }
}
