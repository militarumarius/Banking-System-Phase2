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
    public SplitPaymentTransaction(List<Account> accountsInvolved,
                                   List<Account> accountsNotAccept,
                                   String currency,
                                   String type,
                                   double amount,
                                   Transaction transaction) {
        this.accountsInvolved = accountsInvolved;
        this.accountsNotAccept = accountsNotAccept;
        this.currency = currency;
        this.type = type;
        this.amount = amount;
        this.transaction = transaction;
    }
    public void addTransaction(BankDatabase bank) {
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
        Account errorAccount = bank.checkSplitPayment(accountsInvolved, bank, currency,
                    amountsPerUser);
        for (Account account : accountsInvolved) {
            String description = "Split payment of "
                    + String.format("%.2f", amount)
                    + " " + currency;
            if (errorAccount != null) {
                Transaction errorTransaction = createTransactionError(description, errorAccount.getIBAN());
                account.addTransactionList(errorTransaction);
            } else {
                account.addTransactionList(transaction);
            }
        }
    }
    public Transaction createTransactionError(String description, String iban){
        Transaction errorTransaction = null;
        if (type.equals("custom")) {
             errorTransaction = new TransactionBuilder(transaction.getTimestamp(),
                    description)
                    .involvedAccounts(transaction.getInvolvedAccounts())
                     .amountForUsers(transaction.getAmountForUsers())
                    .error("Account " + iban
                            + " has insufficient funds for a split payment.")
                    .currency(currency)
                    .splitPaymentType(type)
                    .build();
        } else {
            errorTransaction = new TransactionBuilder(transaction.getTimestamp(),
                    description)
                    .involvedAccounts(transaction.getInvolvedAccounts())
                    .amount(transaction.getAmount())
                    .error("Account " + iban
                            + " has insufficient funds for a split payment.")
                    .currency(currency)
                    .splitPaymentType(type)
                    .build();
        }
        return errorTransaction;
    }
}
