package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

public class DeleteCard implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final String cardNumber;

    public DeleteCard(final BankDatabase bank,
                      final CommandInput commandInput, final String cardNumber) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.cardNumber = cardNumber;
    }

    /**
     * method that delete a card
     */
    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        if (user == null) {
            return;
        }
        Account account = user.removeCard(cardNumber);
        if (account == null) {
            return;
        }
        Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                TransactionDescription.CARD_DESTROYED.getMessage())
                .account(account.getIBAN())
                .cardHolder(user.getEmail())
                .card(cardNumber)
                .build();
        account.getTransactions().add(transaction);
    }
}

