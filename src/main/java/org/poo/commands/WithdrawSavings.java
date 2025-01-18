package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.bank.cards.Card;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WithdrawSavings implements Commands{
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;
    private static final int AGE_REQUIRED = 21;

    public WithdrawSavings(final BankDatabase bank,
                     final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that execute the online payment
     */
    @Override
    public void execute() {
        User user = bank.findUserByIban(commandInput.getAccount());
        Account account = user.findAccount(commandInput.getAccount());
        LocalDate birthdayDate = LocalDate.parse(user.getBirthDate());
        if (Period.between(LocalDate.now(), birthdayDate).getYears() < AGE_REQUIRED) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INVALID_AGE.getMessage())
                    .build();
            account.getTransactions().add(transaction);
        }

    }
}
