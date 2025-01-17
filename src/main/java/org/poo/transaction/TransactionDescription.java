package org.poo.transaction;

public enum TransactionDescription {
    ACCOUNT_CREATION_SUCCESS("New account created"),
    CARD_CREATION_SUCCESS("New card created"),
    CARD_DESTROYED("The card has been destroyed"),
    CARD_FROZEN("The card is frozen"),
    CARD_PAYMENT("Card payment"),
    INSUFFICIENT_FUNDS("Insufficient funds"),
    INVALID_DELETE_ACCOUNT("Account couldn't be deleted - there are funds remaining"),
    MINIMUM_FUNDS_REACHED("You have reached the minimum amount of funds, the card will be frozen"),
    INTEREST_RATE_CHANGE("Interest rate of the account changed to ");

    private final String message;

    TransactionDescription(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
