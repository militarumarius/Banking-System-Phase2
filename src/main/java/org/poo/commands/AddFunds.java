package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;

public class AddFunds implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;

    public AddFunds(final BankDatabase bank, final CommandInput commandInput) {
        this.bank = bank;
        this.commandInput = commandInput;
    }

    /**
     * method that execute the addFunds command
     */
    @Override
    public void execute() {
        for (User user : bank.getUsers()) {
            Account account = user.findAccount(commandInput.getAccount());
            if (account != null) {
                account.addBalance(commandInput.getAmount());
            }
        }
    }
}
