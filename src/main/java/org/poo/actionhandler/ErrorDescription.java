package org.poo.actionhandler;

public enum ErrorDescription {

    ACCOUNT_NOT_FOUND("Account not found"),
    CARD_NOT_FOUND("Card not found"),
    USER_NOT_FOUND("User not found"),
    INVALID_ACCOUNT("This is not a savings account"),
    ACCOUNT_DELETED("Account deleted"),
    ACCOUNT_COULD_NOT_BE_DELETED("Account couldn't be deleted "
            + "- see org.poo.transactions for details"),
    INVALID_SPENDING_REPORT("This kind of report is not "
            + "supported for a saving account");

    private final String message;

    ErrorDescription(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}

