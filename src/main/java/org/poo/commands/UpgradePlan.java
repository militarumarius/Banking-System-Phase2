package org.poo.commands;

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

public class UpgradePlan implements Commands{
    private final BankDatabase bank;
    private final CommandInput commandInput;

    public UpgradePlan(final BankDatabase bank,
                        final CommandInput commandInput) {
        this.bank = bank;
        this.commandInput = commandInput;
    }
    @Override
    public void execute() {
        User user = bank.findUserByIban(commandInput.getAccount());
        if (user == null)
            return;
        Account account = bank.findAccountByIban(commandInput.getAccount());
        if (account == null)
            return;
        double exchangeRate = calculateExchangeRate(account);
        if (exchangeRate <= 0) {
            return;
        }
        if(user.getPlan() == null)
            return;
        String planName = user.getPlan().getName();
        int fee = calculateUpgradeFee(commandInput.getNewPlanType(), planName);
        if (!account.validatePayment(fee, exchangeRate)) {
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.INSUFFICIENT_FUNDS.getMessage())
                    .build();
            account.getTransactions().add(transaction);
            return;
        }
        Plan newPlan = PlansFactory.createPlan(commandInput.getNewPlanType());
        if(newPlan == null)
            return;
        user.upgradePlan(newPlan);
        account.subBalance(fee * exchangeRate);

        Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                TransactionDescription.UPGRADE_PLAN.getMessage())
                .newPlanType(commandInput.getNewPlanType())
                .accountIBAN(commandInput.getAccount())
                .build();

        account.addTransactionList(transaction);
    }
    public double calculateExchangeRate(Account account){
        List<String> visited = new ArrayList<>();
        return bank.findExchangeRate("RON",
                account.getCurrency(), visited);
    }

    public int calculateUpgradeFee(String newPlan, String oldPlan) {
        return switch (oldPlan) {
            case "standard", "student" -> switch (newPlan) {
                case "silver" -> 100;
                case "gold" -> 350;
                default -> -1;
            };
            case "silver" -> newPlan.equals("gold") ? 250 : -1;
            default -> -1;
        };
    }
}
