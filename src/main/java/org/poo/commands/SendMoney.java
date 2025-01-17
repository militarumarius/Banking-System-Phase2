package org.poo.commands;

import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

import java.util.ArrayList;
import java.util.List;

public class SendMoney implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;

    public SendMoney(final BankDatabase bank, final CommandInput commandInput) {
        this.bank = bank;
        this.commandInput = commandInput;
    }

    /**
     * method that make an intern transfer
     */
    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        if (user == null) {
            return;
        }
        Account receiver = bank.findAccountByIban(commandInput.getReceiver());
        if (receiver == null) {
            return;
        }
        Account sender = user.findAccount(commandInput.getAccount());
        if (sender == null) {
            return;
        }
        if (!sender.getIBAN().startsWith("RO")) {
            return;
        }
        List<String> visited = new ArrayList<>();
        double exchangeRate = bank.findExchangeRate(sender.getCurrency(),
                receiver.getCurrency(), visited);
        visited.clear();
        if (sender.getBalance() < commandInput.getAmount()) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INSUFFICIENT_FUNDS.getMessage())
                    .build();
            sender.getTransactions().add(transaction);
            return;
        }
        if (exchangeRate <= 0) {
            return;
        }
        sender.subBalance(commandInput.getAmount());
        receiver.addBalance(exchangeRate * commandInput.getAmount());
        String amountSender = String.valueOf(commandInput.getAmount())
                + " " + sender.getCurrency();
        Transaction transactionSender = new TransactionBuilder(commandInput.getTimestamp(),
                commandInput.getDescription())
                .senderIBAN(commandInput.getAccount())
                .receiverIBAN(commandInput.getReceiver())
                .amount(amountSender)
                .transferType("sent")
                .build();
        sender.addTransaction(transactionSender);
        String amountReceiver = String.valueOf(exchangeRate
                * commandInput.getAmount()) + " " + receiver.getCurrency();
        Transaction transactionReceiver = new TransactionBuilder(commandInput.getTimestamp(),
                commandInput.getDescription())
                .senderIBAN(commandInput.getAccount())
                .receiverIBAN(commandInput.getReceiver())
                .amount(amountReceiver)
                .transferType("received")
                .build();
        receiver.addTransaction(transactionReceiver);
    }
}
