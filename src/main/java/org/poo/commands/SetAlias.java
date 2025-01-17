package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;


public class SetAlias implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;

    public SetAlias(final BankDatabase bank,
                    final CommandInput commandInput) {
        this.bank = bank;
        this.commandInput = commandInput;
    }

    /**
     * method that execute the setAlias command
     */
    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        Account account = user.findAccount(commandInput.getAccount());
        bank.getAliasMap().put(commandInput.getAlias(), account);
    }
}
