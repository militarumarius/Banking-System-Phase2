package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.SplitPaymentTransaction;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;

public class AcceptSplitPayment implements Commands{
    private final BankDatabase bank;
    private final CommandInput commandInput;

    public AcceptSplitPayment(final BankDatabase bank,
                        final CommandInput commandInput) {
        this.bank = bank;
        this.commandInput = commandInput;
    }
    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        if (user == null) {
            return;
        }
        SplitPaymentTransaction splitPaymentTransaction = bank.findSplitPaymentByUser(user, commandInput.getSplitPaymentType());
        if (splitPaymentTransaction == null) {
            return;
        }
        Account account = bank.findAccountForSplitPayment(user, commandInput.getSplitPaymentType());
        if (account == null) {
            System.out.println(124);
            return;
        }
        splitPaymentTransaction.getAccountsNotAccept().remove(account);
        if(splitPaymentTransaction.getAccountsNotAccept().isEmpty()) {
            splitPaymentTransaction.addTransaction(bank);
        }

    }
}
