package org.poo.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransactionBuilder {
    private int timestamp;
    private String description;
    private String senderIBAN;
    private String receiverIBAN;
    private Object amount;
    private String transferType;
    private String cardHolder;
    private String account;
    private String card;
    private String commerciant;
    private String currency;
    private String classicAccountIBAN;
    private String savingsAccountIBAN;
    private String role;
    private String newPlanType;
    private String accountIBAN;
    private String error;
    private String splitPaymentType;
    private List<String> involvedAccounts = new ArrayList<>();
    private List<Double> amountForUsers = new ArrayList<>();

    public TransactionBuilder(final int timestamp,
                              final String description) {
        this.timestamp = timestamp;
        this.description = description;
    }

    /**
     *
     */
    public TransactionBuilder senderIBAN(final String newSenderIBAN) {
        this.senderIBAN = newSenderIBAN;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder receiverIBAN(final String newReceiverIBAN) {
        this.receiverIBAN = newReceiverIBAN;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder amount(final Object newAmount) {
        this.amount = newAmount;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder transferType(final String newTransferType) {
        this.transferType = newTransferType;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder account(final String newAccount) {
        this.account = newAccount;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder cardHolder(final String newCardHolder) {
        this.cardHolder = newCardHolder;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder card(final String newCard) {
        this.card = newCard;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder commerciant(final String newCommerciant) {
        this.commerciant = newCommerciant;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder involvedAccounts(final List<String> newInvolvedAccounts) {
        this.involvedAccounts = newInvolvedAccounts;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder currency(final String newCurrency) {
        this.currency = newCurrency;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder error(final String newError) {
        this.error = newError;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder accountIBAN(final String accountIBAN) {
        this.accountIBAN = accountIBAN;
        return this;
    }

    /**
     *
     */
    public TransactionBuilder newPlanType(final String newPlanType) {
        this.newPlanType = newPlanType;
        return this;
    }

    public TransactionBuilder amountForUsers(final List<Double> amountForUsers) {
        this.amountForUsers = amountForUsers;
        return this;
    }

    public TransactionBuilder splitPaymentType(final String splitPaymentType) {
        this.splitPaymentType = splitPaymentType;
        return this;
    }

    public TransactionBuilder role(final String role) {
        this.role = role;
        return this;
    }

    public TransactionBuilder classicAccountIBAN(final String classicAccountIBAN) {
        this.classicAccountIBAN = classicAccountIBAN;
        return this;
    }

    public TransactionBuilder savingsAccountIBAN(final String savingsAccountIBAN) {
        this.savingsAccountIBAN = savingsAccountIBAN;
        return this;
    }

    /** */
    public Transaction build() {
        return new Transaction(this);
    }
}
