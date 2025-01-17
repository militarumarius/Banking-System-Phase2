package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionHandler.ErrorDescription;
import org.poo.actionHandler.ErrorOutput;
import org.poo.actionHandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

public class DeleteAccount implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public DeleteAccount(final BankDatabase bank,
                         final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that execute deleleteAccount command
     */
    @Override
    public void execute() {
        ErrorOutput errorOutput;
        ObjectNode node;
        User user = bank.getUserMap().get(commandInput.getEmail());
        if (user == null) {
            return;
        }
        boolean check = user.getAccounts().removeIf(account ->
                account.getIBAN().equals(commandInput.getAccount()) && account.getBalance() == 0);
        if (check) {
            errorOutput = new ErrorOutput(ErrorDescription.ACCOUNT_DELETED.getMessage(),
                    commandInput.getTimestamp());
            node = errorOutput.toObjectNodeSuccess();
        } else {
            errorOutput = new ErrorOutput(ErrorDescription.
                    ACCOUNT_COULD_NOT_BE_DELETED.getMessage(), commandInput.getTimestamp());
            node = errorOutput.toObjectNodeErrorWithTimestamp();
            Account account = bank.findAccountByIban(commandInput.getAccount());
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INVALID_DELETE_ACCOUNT.getMessage())
                    .build();
            account.addTransaction(transaction);
        }
        PrintOutput deleteAccount = new PrintOutput("deleteAccount",
                node, commandInput.getTimestamp());
        deleteAccount.printCommand(output);
    }
}
