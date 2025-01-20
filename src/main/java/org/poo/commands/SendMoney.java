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
import org.poo.transaction.Commerciant;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

import java.util.ArrayList;
import java.util.List;

public class SendMoney implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public SendMoney(final BankDatabase bank,
                     final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
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
        if (!commandInput.getAccount().startsWith("RO")) {
            return;
        }
        Account receiver = bank.findAccountByIban(commandInput.getReceiver());
        if (receiver == null && !bank.checkCommerciantAccount(commandInput.getReceiver())) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.
                    USER_NOT_FOUND.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput sendMoney = new PrintOutput("sendMoney",
                    node, commandInput.getTimestamp());
            sendMoney.printCommand(output);
            return;
        }
        Account sender = user.findAccount(commandInput.getAccount());
        if (sender == null) {
            return;
        }
        if (!sender.getIBAN().startsWith("RO")) {
            return;
        }
        double commision = accountSubCommision(sender, user);
        if (sender.getBalance() - commision < commandInput.getAmount()) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INSUFFICIENT_FUNDS.getMessage())
                    .build();
            sender.getTransactions().add(transaction);
            return;
        }
        sender.subBalance(commandInput.getAmount());
        sender.subBalance(commision);
        if (!bank.checkCommerciantAccount(commandInput.getReceiver())) {
            List<String> visited = new ArrayList<>();
            double exchangeRate = bank.findExchangeRate(sender.getCurrency(),
                    receiver.getCurrency(), visited);
            visited.clear();
            if (exchangeRate < 0)
                return;
            receiver.addBalance(exchangeRate * commandInput.getAmount());
            String amountReceiver = String.valueOf(exchangeRate
                    * commandInput.getAmount()) + " " + receiver.getCurrency();
            Transaction transactionReceiver = new TransactionBuilder(commandInput.getTimestamp(),
                    commandInput.getDescription())
                    .senderIBAN(commandInput.getAccount())
                    .receiverIBAN(commandInput.getReceiver())
                    .amount(amountReceiver)
                    .transferType("received")
                    .build();
            receiver.addTransactionList(transactionReceiver);
        } else {
            accountAddCashback(sender, commandInput.getAmount());
        }
        String amountSender = String.valueOf(commandInput.getAmount())
                + " " + sender.getCurrency();
        Transaction transactionSender = new TransactionBuilder(commandInput.getTimestamp(),
                commandInput.getDescription())
                .senderIBAN(commandInput.getAccount())
                .receiverIBAN(commandInput.getReceiver())
                .amount(amountSender)
                .transferType("sent")
                .build();
        if (sender.isBusinessAccount() && !sender.getOwner().equals(user))
            return;
        sender.addTransactionList(transactionSender);
//        if (bank.checkCommerciantAccount(commandInput.getReceiver()) && sender.isBusinessAccount()) {
//            Transaction businessTransaction = new TransactionBuilder(commandInput.getTimestamp(),
//                    TransactionDescription.CARD_PAYMENT.getMessage())
//                    .cardHolder(user.getLastName() + " " + user.getFirstName())
//                    .amount(commandInput.getAmount())
//                    .role(user.getRole())
//                    .commerciant(bank.getCommerciantByIban(commandInput.getReceiver()).getCommerciant())
//                    .build();
//            sender.getTransactionsForBusiness().add(businessTransaction);
//        }
    }

    public double calculateExchangeRate(String currency) {
        List<String> visited = new ArrayList<>();
        return bank.findExchangeRate(currency,
                "RON", visited);
    }

    public double accountSubCommision(Account account, User user) {
        double exchangeRateForCommision = calculateExchangeRate(account.getCurrency());
        double amountForCommisionCalculate = commandInput.getAmount() * exchangeRateForCommision;
        return user.calculateCommision(amountForCommisionCalculate) * commandInput.getAmount();
    }

    public void accountAddCashback(Account account, double totalAmount) {
        Commerciant commerciant = bank.getCommerciantByIban(commandInput.getReceiver());
        double exchangeRateForCommision = calculateExchangeRate(account.getCurrency());
        double amountForCommisionCalculate = commandInput.getAmount() * exchangeRateForCommision;
        account.addBalance(commerciant.getCashbackStrategy()
                .calculateCashback(bank, account,
                        amountForCommisionCalculate, totalAmount, commerciant.getType()));
    }
}
