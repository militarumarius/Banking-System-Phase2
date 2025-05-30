package org.poo.bank.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.actionhandler.CommerciantOutput;
import org.poo.actionhandler.UserOutput;
import org.poo.bank.User;
import org.poo.bank.cards.Card;
import org.poo.transaction.Commerciant;
import org.poo.transaction.Transaction;

import java.util.*;

public abstract class Account {
    @JsonProperty("IBAN")
    private final String IBAN;
    private final String type;
    private double balance = 0.0;
    private final String currency;
    @Setter
    private List<Card> cards = new ArrayList<>();
    @Setter
    private List<Transaction> transactions = new ArrayList<>();
    @JsonIgnore
    @Setter
    private double interestRate;
    @JsonIgnore
    @Setter
    private double minAmount = 0.0;
    @JsonIgnore
    @Getter
    private int totalTransaction = 0;
    @JsonIgnore
    @Getter
    private double totalAmount = 0.0;
    @JsonIgnore
    @Getter
    @Setter
    private boolean foodCashback = false;
    @JsonIgnore
    @Getter
    @Setter
    private boolean techCashback = false;
    @JsonIgnore
    @Getter
    @Setter
    private boolean clothesCashback = false;
    @JsonIgnore
    @Getter
    private double spendingLimits = 0.0;

    /**
     *
     */
    @JsonIgnore
    public double getMinAmount() {
        return minAmount;
    }

    protected Account(final String iban, final String type, final String currency) {
        this.IBAN = iban;
        this.type = type;
        this.currency = currency;
        this.interestRate = 0;
    }

    protected Account(final Account account) {
        this.IBAN = account.IBAN;
        this.type = account.type;
        this.balance = account.balance;
        this.currency = account.currency;
        this.totalTransaction = account.totalTransaction;
        this.totalAmount = account.totalAmount;
        this.cards = new ArrayList<>(account.cards);
    }

    /**
     *
     */
    public String getType() {
        return type;
    }

    /**
     *
     */
    public double getBalance() {
        return balance;
    }

    /**
     *
     */
    public void setBalance(final double amount) {
        balance = amount;
        if (amount < 0) {
            throw new RuntimeException("error");
        }
    }

    /**
     *
     */
    public String getCurrency() {
        return currency;
    }

    /**
     *
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     *
     */
    @JsonIgnore
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     *
     */
    @JsonIgnore
    public String getIBAN() {
        return IBAN;
    }

    /**
     * method that check if a payment can be done
     */
    public boolean validatePayment(final double amount, final double exchangeRate) {
        return this.getBalance() >= amount * exchangeRate
                && this.getBalance() > this.getMinAmount();
    }

    /**
     * method that substract the balanced of the account
     */
    public void subBalance(final double amount) {
        balance -= amount;
        if (balance < 0) {
            throw new RuntimeException("error at payment");
        }
    }

    /**
     *
     */
    public void addTransaction() {
        totalTransaction += 1;
    }

    /**
     *
     */
    public void addAmount(final double amount) {
        totalAmount += amount;
    }

    /**
     * method that add an amount in the balanced of the account
     */
    public void addBalance(final double amount) {
        balance += amount;
    }

    /**
     *
     */
    @JsonIgnore
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * method that get the list of the transactions in the given interval
     */
    public List<Transaction> getTransactionsInInterval(final int startTimestamp,
                                                       final int endTimestamp) {
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    /**
     *
     */
    @JsonIgnore
    public List<UserOutput> getEmployees() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public List<UserOutput> getManagers() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     * method that get the list of the spending transactions in the given interval
     */
    public List<Transaction> getSpendingTransaction(final int startTimestamp,
                                                    final int endTimestamp) {
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp
                    && transaction.getDescription().equals("Card payment")) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    /**
     * method that get the commerciants of the given transactions
     */
    public List<Commerciant> getCommerciants(final List<Transaction> listOfTransactions) {
        List<Commerciant> commerciants = new ArrayList<>();
        for (Transaction transaction : listOfTransactions) {
            Commerciant commerciant = new Commerciant(transaction.getCommerciant(),
                    (Double) transaction.getAmount());
            commerciants.add(commerciant);
        }
        return commerciants;
    }

    /**
     * method that add a transaction in the list of the transactions
     */
    public void addTransactionList(final Transaction transaction) {

        this.getTransactions().add(transaction);
    }

    /**
     *
     */
    @JsonIgnore
    public boolean isBusinessAccount() {
        return false;
    }

    /**
     *
     */
    @JsonIgnore
    public List<User> getUsersList() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public double getSpendingLimits() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public double getDepositLimits() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    public void setSpendingLimits(final double limit) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    public void setDepositLimits(final double limit) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public List<Transaction> getTransactionsForBusiness() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public Map<String, Double> calculateTotalSent(final List<Transaction> listOfTransactions) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");

    }

    /**
     *
     */
    @JsonIgnore
    public Map<String, Double> calculateTotalDeposited(final List<Transaction> listOfTransactions) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public double totalSentForReport() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public double totalDepositForReport() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public boolean checkPaymentBusiness(final double amount,
                                        final String role) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public List<String> getManagersUsername(final List<String> usersnameList) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public List<String> getEmployeesUsername(final List<String> usersnameList) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public List<CommerciantOutput> calculateCommerciants(final List<Transaction> listOfTransactions,
                                                         final String owner) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public User getOwner() {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public List<Transaction> getBusinessTransactionFiltered(final int startTimestamp,
                                                            final int endTimestamp) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

    /**
     *
     */
    @JsonIgnore
    public List<String> getAllUserForCommerciantReport(final List<String> users,
                                                       final Map<String, Integer> userPayments) {
        throw new UnsupportedOperationException("Is not a BusinessAccount");
    }

}
