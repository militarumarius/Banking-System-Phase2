package org.poo.transaction;

import lombok.Getter;
import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SplitPaymentTransaction {
    private List<Account> accountsInvolved;
    private List<Account> accountsNotAccept;
    private String currency;
    private String type;
    private double amount;
    private Transaction transaction;

    public SplitPaymentTransaction(final List<Account> accountsInvolved,
                                   final List<Account> accountsNotAccept,
                                   final String currency,
                                   final String type,
                                   final double amount,
                                   final Transaction transaction) {
        this.accountsInvolved = accountsInvolved;
        this.accountsNotAccept = accountsNotAccept;
        this.currency = currency;
        this.type = type;
        this.amount = amount;
        this.transaction = transaction;
    }

    /**
     * method that add transaction when all the users accept the split payment method
     * */
    public void addTransaction(final BankDatabase bank) {
        List<Double> amountsPerUser = getAmountPerUser();
        Account errorAccount = bank.checkSplitPayment(accountsInvolved, bank, currency,
                amountsPerUser);
        if (errorAccount == null) {
            for (int i = 0; i < accountsInvolved.reversed().size(); i++) {
                List<String> visited = new ArrayList<>();
                double exchangeRate = bank.findExchangeRate(currency,
                        accountsInvolved.get(i).getCurrency(), visited);
                visited.clear();
                double amountToPayThisAccount = amountsPerUser.get(i) * exchangeRate;
                accountsInvolved.get(i).subBalance(amountToPayThisAccount);
            }
        }
        for (Account account : accountsInvolved) {
            String description = "Split payment of "
                    + String.format("%.2f", amount)
                    + " " + currency;
            if (errorAccount != null) {
                String error = "Account " + errorAccount.getIBAN()
                        + " has insufficient funds for a split payment.";
                Transaction errorTransaction = createTransactionError(description, error);
                account.addTransactionList(errorTransaction);
            } else {
                account.addTransactionList(transaction);
            }
        }
    }

    /**
     * method that add transaction when one  user reject the split payment method
     * */
    public void rejectTransaction(final BankDatabase bank) {
        List<Double> amountsPerUser = getAmountPerUser();
        for (Account account : accountsInvolved) {
            String description = "Split payment of "
                    + String.format("%.2f", amount)
                    + " " + currency;
            Transaction errorTransaction = createTransactionError(description,
                    TransactionDescription.REJECT_SPLIT_PAYMENT.getMessage());
            account.addTransactionList(errorTransaction);
        }
    }

    /**
     * create a transaction
     * */
    public Transaction createTransactionError(final String description,
                                              final String error) {
        Transaction errorTransaction = null;
        if (type.equals("custom")) {
            errorTransaction = new TransactionBuilder(transaction.getTimestamp(),
                    description)
                    .involvedAccounts(transaction.getInvolvedAccounts())
                    .amountForUsers(transaction.getAmountForUsers())
                    .error(error)
                    .currency(currency)
                    .splitPaymentType(type)
                    .build();
        } else {
            errorTransaction = new TransactionBuilder(transaction.getTimestamp(),
                    description)
                    .involvedAccounts(transaction.getInvolvedAccounts())
                    .amount(transaction.getAmount())
                    .error(error)
                    .currency(currency)
                    .splitPaymentType(type)
                    .build();
        }
        return errorTransaction;
    }

    /** */
    public List<Double> getAmountPerUser() {
        double amountToPay = amount / accountsInvolved.size();
        List<Double> amountsPerUser = new ArrayList<>();
        if (type.equals("equal")) {
            for (int i = 0; i < accountsInvolved.size(); i++) {
                amountsPerUser.add(amountToPay);
            }
        } else {
            for (int i = 0; i < accountsInvolved.size(); i++) {
                amountsPerUser.add(transaction.getAmountForUsers().get(i));
            }
        }
        return amountsPerUser;
    }
}
