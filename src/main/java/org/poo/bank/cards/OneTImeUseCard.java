package org.poo.bank.cards;

import org.poo.bank.accounts.Account;

public class OneTImeUseCard extends Card {
    public OneTImeUseCard(final String cardNumber,
                          final String type,
                          final Account account) {
        super(cardNumber, type, account);
    }
}
