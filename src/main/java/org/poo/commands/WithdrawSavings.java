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
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class WithdrawSavings implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;
    private static final int AGE_REQUIRED = 21;

    public WithdrawSavings(final BankDatabase bank,
                           final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that execute the withdraw of savings
     */
    @Override
    public void execute() {
        User user = bank.findUserByIban(commandInput.getAccount());
        if (user == null) {
            return;
        }
        Account account = user.findAccount(commandInput.getAccount());
        if (account == null) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.
                    ACCOUNT_NOT_FOUND.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput acceptSplitPayment = new PrintOutput("withdrawSavings",
                    node, commandInput.getTimestamp());
            acceptSplitPayment.printCommand(output);
            return;
        }
        LocalDate birthdayDate = LocalDate.parse(user.getBirthDate());
        if (Period.between(birthdayDate, LocalDate.now()).getYears() < AGE_REQUIRED) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INVALID_AGE.getMessage())
                    .build();
            account.getTransactions().add(transaction);
            return;
        }
        Account withdrawAccount = user.findAccountForWithdrawSavings(commandInput.getCurrency());
        if (withdrawAccount == null) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INVALID_WITHDRAW_SAVINGS.getMessage())
                    .build();
            account.getTransactions().add(transaction);
            return;
        }
        double exchangeRate = calculateExchangeRate(account.getCurrency());
        if (exchangeRate < 0) {
            return;
        }
        if (account.getBalance() < commandInput.getAmount()) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INSUFFICIENT_FUNDS.getMessage())
                    .build();
            account.getTransactions().add(transaction);
            return;
        }
        account.subBalance(commandInput.getAmount());
        withdrawAccount.addBalance(exchangeRate * commandInput.getAmount());
        Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                TransactionDescription.SAVINGS_WITHDRAW.getMessage())
                .amount(commandInput.getAmount())
                .classicAccountIBAN(withdrawAccount.getIBAN())
                .savingsAccountIBAN(account.getIBAN())
                .build();
        account.getTransactions().add(transaction);
        withdrawAccount.getTransactions().add(transaction);

    }
    /** */
    public double calculateExchangeRate(final String currency) {
        List<String> visited = new ArrayList<>();
        return bank.findExchangeRate(currency,
                 commandInput.getCurrency(), visited);
    }
}
