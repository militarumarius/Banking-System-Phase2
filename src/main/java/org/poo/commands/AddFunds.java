package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

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
        User user = bank.getUserMap().get(commandInput.getEmail());
        Account account = user.findAccount(commandInput.getAccount());
        if (account != null) {
            if (account.isBusinessAccount() && account.checkPaymentBusiness(commandInput.getAmount(), user.getRole())) {
                Transaction businessTransaction = new TransactionBuilder(commandInput.getTimestamp(),
                        TransactionDescription.ADD_FUND.getMessage())
                        .cardHolder(user.getLastName() + " " + user.getFirstName())
                        .amount(commandInput.getAmount())
                        .role(user.getRole())
                        .build();
                account.getTransactionsForBusiness().add(businessTransaction);
                account.addBalance(commandInput.getAmount());
            }
            if (!account.isBusinessAccount())
                account.addBalance(commandInput.getAmount());
        }
    }
}
