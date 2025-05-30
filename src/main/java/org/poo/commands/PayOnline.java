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
import org.poo.bank.plans.Plan;
import org.poo.bank.plans.PlansFactory;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Commerciant;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

import java.util.ArrayList;
import java.util.List;

public class PayOnline implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public PayOnline(final BankDatabase bank,
                     final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that execute the online payment
     */
    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        if (user == null) {
            return;
        }
        Account account = user.getCardAccountMap().get(commandInput.getCardNumber());
        if (commandInput.getAmount() == 0.0) {
            return;
        }
        if (account == null) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.
                    CARD_NOT_FOUND.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput payOnline = new PrintOutput("payOnline",
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
        double exchangeRate = bank.findExchangeRate(commandInput.getCurrency(),
                account.getCurrency(), visited);
        if (CashWithdrawal.insuficientFundsError(account, exchangeRate, commandInput)) {
            return;
        }
        double totalAmount = commandInput.getAmount() * exchangeRate;
        accountSubCommision(account, user, totalAmount);
        Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                TransactionDescription.CARD_PAYMENT.getMessage())
                .amount(totalAmount)
                .commerciant(commandInput.getCommerciant())
                .build();
        if ((account.isBusinessAccount()
                && !account.getOwner().equals(user))
                || (!account.isBusinessAccount())) {
            account.getTransactions().add(transaction);
        }
        if (card.getType().equals("OneTimeCard")) {
            DeleteCard deleteCard = new DeleteCard(bank, commandInput, card.getCardNumber());
            deleteCard.execute();
            CreateOneTimeCard command = new CreateOneTimeCard(bank, commandInput,
                    account.getIBAN());
            command.execute();
        }
        if (account.isBusinessAccount()) {
            Transaction businessTransaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.CARD_PAYMENT.getMessage())
                    .cardHolder(user.getLastName() + " " + user.getFirstName())
                    .amount(totalAmount)
                    .role(user.getRole())
                    .commerciant(commandInput.getCommerciant())
                    .build();
            account.getTransactionsForBusiness().add(businessTransaction);
        }
        accountAddCashback(account, totalAmount);
        account.addTransaction();
        upgradeGoldPlan(account, user, totalAmount);
    }

    /**
     *
     */
    public double calculateExchangeRate(final String currency) {
        List<String> visited = new ArrayList<>();
        return bank.findExchangeRate(currency,
                "RON", visited);
    }

    /**
     *
     */
    public void accountSubCommision(final Account account,
                                    final User user,
                                    final double amount) {
        double exchangeRateForCommision = calculateExchangeRate(commandInput.getCurrency());
        double amountForCommisionCalculate = amount * exchangeRateForCommision;
        double commision = user.calculateCommision(amountForCommisionCalculate) * amount;
        account.subBalance(commision);
    }

    /**
     *
     */
    public void accountAddCashback(final Account account,
                                   final double totalAmount) {
        Commerciant commerciant = bank.findCommerciant(commandInput.getCommerciant());
        double exchangeRateForCommision = calculateExchangeRate(commandInput.getCurrency());
        double amountForCommisionCalculate = commandInput.getAmount() * exchangeRateForCommision;
        account.addBalance(commerciant.getCashbackStrategy()
                .calculateCashback(bank, account,
                        amountForCommisionCalculate, totalAmount, commerciant.getType()));
    }

    /**
     *
     */
    public void upgradeGoldPlan(final Account account,
                                final User user,
                                final double amount) {
        double exchangeRateForCommision = calculateExchangeRate(commandInput.getCurrency());
        double amountForCommisionCalculate = amount * exchangeRateForCommision;
        if (user.checkUpgradeGoldPlan(amountForCommisionCalculate)) {
            Plan newPlan = PlansFactory.createPlan("gold");
            if (newPlan == null) {
                return;
            }
            user.upgradePlan(newPlan);
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.UPGRADE_PLAN.getMessage())
                    .newPlanType("gold")
                    .accountIBAN(account.getIBAN())
                    .build();
            account.addTransactionList(transaction);
        }
    }

}
