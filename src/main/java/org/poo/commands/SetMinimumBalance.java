package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;


public class SetMinimumBalance implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;

    public SetMinimumBalance(final BankDatabase bank, final CommandInput commandInput) {
        this.bank = bank;
        this.commandInput = commandInput;
    }

    /**
     * method that execute the setMinimumBalance command
     */
    @Override
    public void execute() {
        Account account = bank.findAccountByIban(commandInput.getAccount());
        account.setMinAmount(commandInput.getMinBalance());
    }
}
