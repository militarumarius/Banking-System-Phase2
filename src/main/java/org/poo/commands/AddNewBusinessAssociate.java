package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.bank.cards.Card;
import org.poo.fileio.CommandInput;

public class AddNewBusinessAssociate implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;

    public AddNewBusinessAssociate(final BankDatabase bank,
                          final CommandInput commandInput) {
        this.bank = bank;
        this.commandInput = commandInput;
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
        user.setRole(commandInput.getRole());
        user.addAccount(account);
        if (!account.getUsersList().contains(user)) {
            account.getUsersList().add(user);
            for (Card card : account.getCards()) {
                user.addCardforBusiness(account, card);
            }
        }

    }
}
