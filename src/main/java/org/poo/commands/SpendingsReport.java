package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Commerciant;
import org.poo.transaction.Transaction;

import java.util.Comparator;
import java.util.List;

public class SpendingsReport implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public SpendingsReport(final BankDatabase bank,
                           final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that execute the spendingReport command
     */
    @Override
    public void execute() {
        Account account = bank.findAccountByIban(commandInput.getAccount());
        if (account == null) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.ACCOUNT_NOT_FOUND.
                    getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput report = new PrintOutput("spendingsReport",
                    node, commandInput.getTimestamp());
            report.printCommand(output);
            return;
        }
        if (bank.checkSaving(account)) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.
                    INVALID_SPENDING_REPORT.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeErrorWithoutTimestamp();
            PrintOutput report = new PrintOutput("spendingsReport",
                    node, commandInput.getTimestamp());
            report.printCommand(output);
            return;
        }
        List<Transaction> filteredTransactions = account.
                getSpendingTransaction(commandInput.getStartTimestamp(),
                        commandInput.getEndTimestamp());
        List<Commerciant> commerciants = account.
                getCommerciants(filteredTransactions);
        commerciants.sort(Comparator.comparing(Commerciant::getCommerciant));
        PrintOutput spendingsReport = new PrintOutput("spendingsReport",
                PrintOutput.createOutputSpendingTransactionObject(filteredTransactions,
                        commerciants, account),
                commandInput.getTimestamp());
        spendingsReport.printCommand(output);
    }
}
