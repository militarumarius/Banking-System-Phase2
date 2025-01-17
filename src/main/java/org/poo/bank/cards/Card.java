package org.poo.bank.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.bank.accounts.Account;

@Getter @Setter
public abstract class Card {
    private final String cardNumber;
    private String status;
    @JsonIgnore
    private final String type;
    @JsonIgnore
    private final Account account;
    public Card(final String cardNumber, final String type, final Account account) {
        this.cardNumber = cardNumber;
        this.status = "active";
        this.type = type;
        this.account = account;
    }
}
