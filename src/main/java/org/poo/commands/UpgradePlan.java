package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.bank.plans.Plan;
import org.poo.bank.plans.PlansFactory;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

import java.util.ArrayList;
import java.util.List;

public class UpgradePlan implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;
    private static final int SILVER_FEE = 250;
    private static final int GOLD_FEE = 350;
    private static final int SILVER_FEE_LOWER = 100;


    public UpgradePlan(final BankDatabase bank,
                       final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /** */
    @Override
    public void execute() {
        User user = bank.findUserByIban(commandInput.getAccount());
        if (user == null) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.
                    ACCOUNT_NOT_FOUND.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput upgradePlan = new PrintOutput("upgradePlan",
                    node, commandInput.getTimestamp());
            upgradePlan.printCommand(output);
            return;
        }
        Account account = bank.findAccountByIban(commandInput.getAccount());
        if (account == null) {
            return;
        }
        double exchangeRate = calculateExchangeRate(account);
        if (exchangeRate <= 0) {
            return;
        }
        if (user.getPlan() == null) {
            return;
        }
        String planName = user.getPlan().getName();
        if (user.getPlan().getName().equals(commandInput.getNewPlanType())) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.PLAN_ALREADY_HAVE.getMessage()
                            + commandInput.getNewPlanType() + " plan.")
                    .build();
            account.getTransactions().add(transaction);
            return;
        }
        if (!user.userCheckUpgradePlan(planName)) {
            return;
        }
        int fee = calculateUpgradeFee(commandInput.getNewPlanType(), user.getPlan());
        if (fee <= 0) {
            return;
        }
        if (!account.validatePayment(fee, exchangeRate)) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INSUFFICIENT_FUNDS.getMessage())
                    .build();
            account.getTransactions().add(transaction);
            return;
        }
        Plan newPlan = PlansFactory.createPlan(commandInput.getNewPlanType());
        if (newPlan == null) {
            return;
        }
        user.upgradePlan(newPlan);
        account.subBalance(fee * exchangeRate);
        Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                TransactionDescription.UPGRADE_PLAN.getMessage())
                .newPlanType(commandInput.getNewPlanType())
                .accountIBAN(commandInput.getAccount())
                .build();

        account.addTransactionList(transaction);
    }

    /** */
    public double calculateExchangeRate(final Account account) {
        List<String> visited = new ArrayList<>();
        return bank.findExchangeRate("RON",
                account.getCurrency(), visited);
    }

    /** */
    public int calculateUpgradeFee(final String newPlan,
                                   final Plan oldPlan) {
        return switch (newPlan) {
            case "gold" -> oldPlan.getFeeUpgradeGold();
            case "silver" -> oldPlan.getFeeUpgradeSilver();
            default -> -1;
        };
    }
}
