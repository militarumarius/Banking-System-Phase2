package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;

import java.util.List;

public class BusinessReport implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public BusinessReport(final BankDatabase bank,
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
            PrintOutput report = new PrintOutput("businessReport", node,
                    commandInput.getTimestamp());
            report.printCommand(output);
            return;
        }
        if (ChangeSpendingLimit.errorNotBusinessType(account, commandInput, output)) {
            return;
        }
        if (commandInput.getType().equals("transaction")) {
            PrintOutput businessReport = new PrintOutput("businessReport",
                    PrintOutput.createOutputBusinessReportTransactions(account.getManagers(),
                            account.getEmployees(), account, account.totalSentForReport(),
                            account.totalDepositForReport()),
                    commandInput.getTimestamp());
            businessReport.printCommand(output);
            return;
        }
        User owner = account.getOwner();
        String ownerUsername = owner.getLastName() + " " + owner.getFirstName();
        List<Transaction> filteredTransactionsForBusiness = account.
                getBusinessTransactionFiltered(commandInput.getStartTimestamp(),
                        commandInput.getEndTimestamp());
        PrintOutput businessReport = new PrintOutput("businessReport",
                PrintOutput.createOutputBusinessReportCommerciant(account,
                        account.calculateCommerciants(filteredTransactionsForBusiness,
                                ownerUsername)),
                commandInput.getTimestamp());
        businessReport.printCommand(output);
    }
}
