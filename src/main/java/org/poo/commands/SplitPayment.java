package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.SplitPaymentTransaction;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;

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
        List<Account> accounts = bank.convertAccountfromString(commandInput.getAccounts());
        List<Account> accountsInvolved = bank.convertAccountfromString(commandInput.getAccounts());
        Transaction transaction;
        String description = "Split payment of "
                + String.format("%.2f", commandInput.getAmount())
                + " " + commandInput.getCurrency();
        if (commandInput.getSplitPaymentType().equals("custom")) {
            transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    description)
                    .amountForUsers(commandInput.getAmountForUsers())
                    .involvedAccounts(commandInput.getAccounts())
                    .splitPaymentType(commandInput.getSplitPaymentType())
                    .currency(commandInput.getCurrency())
                    .build();
        } else {
            transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    description)
                    .amount(commandInput.getAmount()
                            / commandInput.getAccounts().size())
                    .involvedAccounts(commandInput.getAccounts())
                    .splitPaymentType(commandInput.getSplitPaymentType())
                    .currency(commandInput.getCurrency())
                    .build();
        }

        SplitPaymentTransaction splitPayment = new SplitPaymentTransaction(accounts,
                accountsInvolved,
                commandInput.getCurrency(), commandInput.getSplitPaymentType(),
                commandInput.getAmount(), transaction);
        bank.getSplitPayments().add(splitPayment);
    }
}
