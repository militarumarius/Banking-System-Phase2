package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.bank.cards.Card;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Commerciant;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

import java.util.ArrayList;
import java.util.List;

public class CashWithdrawal implements Commands{
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public CashWithdrawal(final BankDatabase bank,
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
        Account account = user.getCardAccountMap().get(commandInput.getCardNumber());
        if (account == null) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.
                    CARD_NOT_FOUND.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput payOnline = new PrintOutput("cashWithdrawal",
                    node, commandInput.getTimestamp());
            payOnline.printCommand(output);
            return;
        }
        Card card = user.findCard(commandInput.getCardNumber());
        if (card == null) {
            return;
        }
        if (card.getStatus().equals("frozen")) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.CARD_FROZEN.getMessage())
                    .build();
            card.getAccount().getTransactions().add(transaction);
            return;
        }
        List<String> visited = new ArrayList<>();
        double exchangeRate = bank.findExchangeRate("RON",
                account.getCurrency(), visited);
        if (!account.validatePayment(commandInput.getAmount(), exchangeRate)) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INSUFFICIENT_FUNDS.getMessage())
                    .build();
            account.getTransactions().add(transaction);
            return;
        }
        if (exchangeRate <= 0) {
            return;
        }
        double totalAmount = commandInput.getAmount() * exchangeRate;
        account.subBalance(totalAmount);
        accountSubCommision(account, user);
        Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                TransactionDescription.CASH_WITHDRAWAL.getMessage() + commandInput.getAmount())
                .amount( commandInput.getAmount())
                .commerciant(commandInput.getCommerciant())
                .build();
        account.getTransactions().add(transaction);
    }
    public double calculateExchangeRate(Account account){
        List<String> visited = new ArrayList<>();
        return bank.findExchangeRate("RON",
                account.getCurrency(), visited);
    }

    public void accountSubCommision(Account account, User user) {
        double exchangeRateForCommision = calculateExchangeRate(account);
        double amountForCommisionCalculate = commandInput.getAmount();
        double commision = user.calculateCommision(amountForCommisionCalculate)
                * commandInput.getAmount() * exchangeRateForCommision;
        account.subBalance(commision);
    }
}
