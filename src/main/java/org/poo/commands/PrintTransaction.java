package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

public class PrintTransaction implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public PrintTransaction(final BankDatabase bank,
                            final CommandInput commandInput,
                            final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that print the transactions
     * */
    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        PrintOutput printTransactions = new PrintOutput("printTransactions",
                commandInput.getTimestamp(), user);
        printTransactions.printTransaction(output);
    }
}
