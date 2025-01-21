package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

public class ChangeInterestRate implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public ChangeInterestRate(final BankDatabase bank,
                              final CommandInput commandInput,
                              final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that change the interest rate of the economy account
     */
    @Override
    public void execute() {
        Account account = bank.findAccountByIban(commandInput.getAccount());
        if (account == null) {
            return;
        }
        if (!bank.checkSaving(account)) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription
                    .INVALID_ACCOUNT.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput changeInterestRate = new PrintOutput("changeInterestRate",
                    node, commandInput.getTimestamp());
            changeInterestRate.printCommand(output);
            return;
        }
        Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                TransactionDescription.INTEREST_RATE_CHANGE.getMessage()
                        + commandInput.getInterestRate())
                .build();
        account.setInterestRate(commandInput.getInterestRate());
        account.addTransactionList(transaction);
    }
}
