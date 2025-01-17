package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.actionHandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.fileio.CommandInput;

public class PrintUsers implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public PrintUsers(final BankDatabase bank,
                      final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that print the users
     */
    @Override
    public void execute() {
        PrintOutput printUsers = new PrintOutput("printUsers",
                bank.copyUsers(), commandInput.getTimestamp());
        printUsers.printCommand(output);
    }
}

