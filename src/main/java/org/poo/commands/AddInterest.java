package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;

public class AddInterest implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public AddInterest(final BankDatabase bank,
                       final CommandInput commandInput,
                       final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that execute the addInterest command
     */
    @Override
    public void execute() {
        Account account = bank.findAccountByIban(commandInput.getAccount());
        if (!bank.checkSaving(account)) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription
                    .INVALID_ACCOUNT.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput addInterest = new PrintOutput("addInterest",
                    node, commandInput.getTimestamp());
            addInterest.printCommand(output);
            return;
        }
        account.addBalance(account.getBalance() * commandInput.getInterestRate());
    }
}
