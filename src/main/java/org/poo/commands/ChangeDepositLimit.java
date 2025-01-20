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

public class ChangeDepositLimit implements Commands{
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public ChangeDepositLimit(final BankDatabase bank,
                               final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }
    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        if (user == null) {
            return;
        }
        Account account = bank.findAccountByIban(commandInput.getAccount());
        if (account == null) {
            return;
        }
        if (!account.isBusinessAccount()){
            return;
        }
        if (!user.getRole().equals("owner")) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.
                    ERROR_BUSINESS_LIMIT_CHANGE.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput changeSpendingLimit = new PrintOutput("changeSpendingLimit",
                    node, commandInput.getTimestamp());
            changeSpendingLimit.printCommand(output);
            return;
        }
        account.setDepositLimits(commandInput.getAmount());
    }
}
