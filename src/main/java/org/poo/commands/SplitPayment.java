package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;

import java.util.ArrayList;
import java.util.List;

public class SplitPayment implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;

    public SplitPayment(final BankDatabase bank,
                        final CommandInput commandInput) {
        this.bank = bank;
        this.commandInput = commandInput;
    }

    /**
     * methot that execute the split payment
     */
    @Override
    public void execute() {
        double amountToPay = commandInput.getAmount() / commandInput.getAccounts().size();
        List<Account> accounts = bank.convertAccountfromString(commandInput.getAccounts());
        Account errorAccount = bank.checkSplitPayment(accounts, bank, commandInput, amountToPay);
        for (Account account : accounts) {
            List<String> visited = new ArrayList<>();
            double exchangeRate = bank.findExchangeRate(commandInput.getCurrency(),
                    account.getCurrency(), visited);
            visited.clear();
            double amountToPayThisAccount = amountToPay * exchangeRate;
            String description = "Split payment of "
                    + String.format("%.2f", commandInput.getAmount())
                    + " " + commandInput.getCurrency();
            if (errorAccount != null) {
                Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                        description)
                        .involvedAccounts(commandInput.getAccounts())
                        .error("Account " + errorAccount.getIBAN()
                                + " has insufficient funds for a split payment.")
                        .amount(amountToPay)
                        .currency(commandInput.getCurrency())
                        .build();
                account.addTransactionList(transaction);
            } else {
                account.subBalance(amountToPayThisAccount);
                Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                        description)
                        .involvedAccounts(commandInput.getAccounts())
                        .amount(amountToPay)
                        .currency(commandInput.getCurrency())
                        .build();
                account.addTransactionList(transaction);
            }
        }
    }
}
