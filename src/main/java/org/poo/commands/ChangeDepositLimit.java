package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;

public class ChangeDepositLimit implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public ChangeDepositLimit(final BankDatabase bank,
                              final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /** */
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
        if (!account.isBusinessAccount()) {
            return;
        }
        if (ChangeSpendingLimit.errorLimitChange(user, commandInput, output)) {
            return;
        }
        account.setDepositLimits(commandInput.getAmount());
    }
}
