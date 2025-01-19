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

import java.util.*;

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
        List<Commerciant> commerciantsFiltered
                = createFilteredCommerciants(account,filteredTransactions);
        PrintOutput spendingsReport = new PrintOutput("spendingsReport",
                PrintOutput.createOutputSpendingTransactionObject(filteredTransactions,
                        commerciantsFiltered, account),
                commandInput.getTimestamp());
        spendingsReport.printCommand(output);
    }
    public List<Commerciant> createFilteredCommerciants(Account account, List<Transaction> filteredTransactions){
        List<Commerciant> commerciants = account.
                getCommerciants(filteredTransactions);
        Map<String, Double> commerciantsMap = new HashMap<>();
        for (Commerciant commerciant : commerciants) {
            commerciantsMap.put(
                    commerciant.getCommerciant(),
                    commerciantsMap.getOrDefault(commerciant.getCommerciant(), 0.0) + commerciant.getTotal()
            );
        }
        List<Commerciant> commerciantsFiltered = new ArrayList<>();
        for (Map.Entry<String, Double> entry : commerciantsMap.entrySet()) {
            commerciantsFiltered.add(new Commerciant(entry.getKey(), entry.getValue()));
        }
        commerciantsFiltered.sort(Comparator.comparing(Commerciant::getCommerciant));
        return commerciantsFiltered;
    }
}
