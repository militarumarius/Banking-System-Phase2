package org.poo.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @JsonInclude(JsonInclude.Include.NON_EMPTY)
public  class Transaction {
    private int timestamp;
    private String description;
    private String senderIBAN;
    private String receiverIBAN;
    private Object amount;
    private String transferType;
    private String cardHolder;
    private String account;
    private String newPlanType;
    private String accountIBAN;
    private String card;
    private String commerciant;
    private String currency;
    private String error;
    private List<String> involvedAccounts = new ArrayList<>();

    public Transaction(final TransactionBuilder builder) {
        this.timestamp = builder.getTimestamp();
        this.amount = builder.getAmount();
        this.description = builder.getDescription();
        this.receiverIBAN = builder.getReceiverIBAN();
        this.senderIBAN = builder.getSenderIBAN();
        this.transferType = builder.getTransferType();
        this.account = builder.getAccount();
        this.cardHolder = builder.getCardHolder();
        this.card = builder.getCard();
        this.commerciant = builder.getCommerciant();
        this.involvedAccounts = builder.getInvolvedAccounts();
        this.currency = builder.getCurrency();
        this.error = builder.getError();
        this.accountIBAN = builder.getAccountIBAN();
        this.newPlanType = builder.getNewPlanType();
    }

    public Transaction(final Transaction transaction) {
        this.timestamp = transaction.getTimestamp();
        this.transferType = transaction.getTransferType();
        this.description = transaction.getDescription();
        this.receiverIBAN = transaction.getReceiverIBAN();
        this.senderIBAN = transaction.getSenderIBAN();
        this.amount = transaction.getAmount();
        this.cardHolder = transaction.getCardHolder();
        this.account = transaction.getAccount();
        this.card = transaction.getCard();
        this.commerciant = transaction.getCommerciant();
        this.involvedAccounts = transaction.getInvolvedAccounts();
        this.currency = transaction.getCurrency();
        this.error = transaction.getError();
        this.newPlanType = transaction.getNewPlanType();
        this.accountIBAN = transaction.getAccountIBAN();
    }

}
