package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.bank.cards.Card;
import org.poo.bank.cards.OneTImeUseCard;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

import static org.poo.utils.Utils.generateCardNumber;

public class CreateOneTimeCard implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final String iban;

    public CreateOneTimeCard(final BankDatabase bank,
                             final CommandInput commandInput,
                             final String iban) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.iban = iban;
    }

    /**
     * method that create a one time card
     */
    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        if (user == null) {
            return;
        }
        Account account = user.findAccount(iban);
        Card newCard = new OneTImeUseCard(generateCardNumber(), "OneTimeCard", account);
        user.addCard(account, newCard);
        Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                TransactionDescription.CARD_CREATION_SUCCESS.getMessage())
                .account(account.getIBAN())
                .cardHolder(user.getEmail())
                .card(newCard.getCardNumber())
                .build();
        account.addTransaction(transaction);
    }
}
