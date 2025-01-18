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

import java.util.List;

public class Report implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public Report(final BankDatabase bank,
                  final CommandInput commandInput,
                  final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that execute the report command
     */
    @Override
    public void execute() {
        Account account = bank.findAccountByIban(commandInput.getAccount());
        if (account == null) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription
                    .ACCOUNT_NOT_FOUND.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput report = new PrintOutput("report", node,
                    commandInput.getTimestamp());
            report.printCommand(output);
            return;
        }
        List<Transaction> filteredTransactions = account.
                getTransactionsInInterval(commandInput.getStartTimestamp(),
                        commandInput.getEndTimestamp());
        PrintOutput report = new PrintOutput("report",
                PrintOutput.createOutputTransactionObject(filteredTransactions, account),
                commandInput.getTimestamp());
        report.printCommand(output);
    }
}
