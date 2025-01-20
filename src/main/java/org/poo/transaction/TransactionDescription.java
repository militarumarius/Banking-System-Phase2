package org.poo.transaction;

public enum TransactionDescription {
    ACCOUNT_CREATION_SUCCESS("New account created"),
    ADD_FUND("Add funds"),
    CARD_CREATION_SUCCESS("New card created"),
    CARD_DESTROYED("The card has been destroyed"),
    CARD_FROZEN("The card is frozen"),
    CARD_PAYMENT("Card payment"),
    INSUFFICIENT_FUNDS("Insufficient funds"),
    INVALID_DELETE_ACCOUNT("Account couldn't be deleted - there are funds remaining"),
    MINIMUM_FUNDS_REACHED("You have reached the minimum amount of funds, the card will be frozen"),
    PLAN_ALREADY_HAVE("The user already has the "),
    SAVINGS_WITHDRAW("Savings withdrawal"),
    INTEREST_RATE_CHANGE("Interest rate of the account changed to "),
    UPGRADE_PLAN("Upgrade plan"),
    CASH_WITHDRAWAL("Cash withdrawal of "),
    INTEREST_RATE_INCOME("Interest rate income"),
    INVALID_WITHDRAW_SAVINGS("You do not have a classic account."),
    REJECT_SPLIT_PAYMENT("One user rejected the payment."),
    INVALID_AGE("You don't have the minimum age required.");

    private final String message;

    TransactionDescription(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
