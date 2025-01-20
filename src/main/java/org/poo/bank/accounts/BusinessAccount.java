package org.poo.bank.accounts;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.poo.actionhandler.CommerciantOutput;
import org.poo.actionhandler.UserOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.transaction.Transaction;

import java.util.*;

public class BusinessAccount extends Account {
    @Getter @JsonIgnore
    public List<User> users = new ArrayList<>();
    @JsonIgnore
    @Getter
    @Setter
    public double spendingLimits;
    @JsonIgnore
    @Getter
    @Setter
    public double depositLimits;
    @JsonIgnore
    private List<Transaction> transactionsForBusiness = new ArrayList<>();
    @JsonIgnore
    private double exchangeRate;

    @Override
    public User getOwner() {
        return owner;
    }

    @JsonIgnore
    private User owner;

    protected BusinessAccount(BusinessAccount account) {
        super(account);
        this.users = account.getUsersList();
        this.owner = account.getOwner();
    }

    protected BusinessAccount(String iban, String type, String currency, String email, BankDatabase bank) {
        super(iban, type, currency);
        User user = bank.getUserMap().get(email);
        user.setRole("owner");
        this.owner = user;
        this.users.add(user);
        List<String> visited = new ArrayList<>();
        exchangeRate = bank.findExchangeRate("RON",
                currency, visited);
        spendingLimits = 500 * exchangeRate;
        depositLimits = 500 * exchangeRate;
    }

    @Override
    public boolean isBusinessAccount() {
        return true;
    }

    @Override @JsonIgnore
    public List<User> getUsersList() {
        return users;
    }

    @Override
    public double getSpendingLimits() {
        return spendingLimits;
    }

    @Override
    public double getDepositLimits() {
        return depositLimits;
    }

    @Override
    public void setSpendingLimits(double limit) {
        this.spendingLimits = limit;
    }


    @Override
    public void setDepositLimits(double limit) {
        this.depositLimits = limit;
    }

    @Override
    public List<UserOutput> getEmployees() {
        Map<String, Double> totalSpent = calculateTotalSent(transactionsForBusiness);
        Map<String, Double> totalDeposit = calculateTotalDeposited(transactionsForBusiness);
        List<UserOutput> employees = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("employee")) {
                makeUserOutputList(employees, totalSpent, totalDeposit, user);
            }
        }
        return employees;
    }

    @Override
    public List<UserOutput> getManagers() {
        List<UserOutput> managers = new ArrayList<>();
        Map<String, Double> totalSpent = calculateTotalSent(transactionsForBusiness);
        Map<String, Double> totalDeposit = calculateTotalDeposited(transactionsForBusiness);
        for (User user : users) {
            if (user.getRole().equals("manager")) {
                makeUserOutputList(managers, totalSpent, totalDeposit, user);
            }
        }
        return managers;
    }

    private void makeUserOutputList(List<UserOutput> managers, Map<String, Double> totalSpent, Map<String, Double> totalDeposit, User user) {
        String username = user.getLastName() + " " + user.getFirstName();
        UserOutput userOutput;
        double sent = 0.0;
        double deposited = 0.0;
        if (totalDeposit.containsKey(username))
            deposited = totalDeposit.get(username);
        if (totalSpent.containsKey(username))
            sent = totalSpent.get(username);
        userOutput = new UserOutput(username, sent, deposited);
        managers.add(userOutput);
    }

    @Override
    public List<Transaction> getTransactionsForBusiness() {
        return transactionsForBusiness;
    }

    @Override
    public Map<String, Double> calculateTotalSent(List<Transaction> transactions) {
        Map<String, Double> userSpentMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String cardHolder = transaction.getCardHolder();
            double amount = (double) transaction.getAmount();
            String description = transaction.getDescription();
            if (description.equals("Card payment") && !transaction.getRole().equals("owner")) {
                userSpentMap.put(cardHolder, userSpentMap.getOrDefault(cardHolder, 0.0) + amount);
            }
        }
        return userSpentMap;
    }

    @Override
    public Map<String, Double> calculateTotalDeposited(List<Transaction> transactions) {
        Map<String, Double> userDepositedMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            String cardHolder = transaction.getCardHolder();
            double amount = (double) transaction.getAmount();
            String description = transaction.getDescription();
            if (description.equals("Add funds") && !transaction.getRole().equals("owner")) {
                userDepositedMap.put(cardHolder, userDepositedMap.getOrDefault(cardHolder, 0.0) + amount);
            }
        }
        return userDepositedMap;
    }

    @Override
    public double totalSentForReport() {
        double total = 0.0;
        Map<String, Double> totalSpent = calculateTotalSent(transactionsForBusiness);
        for (Map.Entry<String, Double> entry : totalSpent.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    @Override
    public double totalDepositForReport() {
        double total = 0.0;
        Map<String, Double> totalDeposit = calculateTotalDeposited(transactionsForBusiness);
        for (Map.Entry<String, Double> entry : totalDeposit.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    @Override
    public boolean checkPaymentBusiness(double amount, String type) {
        if (type.equals("employee") && amount * 1 / exchangeRate > depositLimits) {
            return false;
        }
        return true;
    }

    @Override
    public List<String> getManagersUsername(List<String> usersnameList) {
        List<String> managers = new ArrayList<>();
        for (User user : users) {
            String username = user.getLastName() + " " + user.getFirstName();
            if (user.getRole().equals("manager") && usersnameList.contains(username)) {
                managers.add(username);
            }
        }
        return managers;
    }

    @Override
    public List<String> getEmployeesUsername(List<String> usersnameList) {
        List<String> employees = new ArrayList<>();
        for (User user : users) {
            String username = user.getLastName() + " " + user.getFirstName();
            if (user.getRole().equals("employee") && usersnameList.contains(username)) {
                employees.add(username);
            }
        }
        return employees;
    }

    @Override
    public List<CommerciantOutput> calculateCommerciants(List<Transaction> transactions, String owner) {
        Map<String, Double> commerciantTotalReceived = new HashMap<>();
        Map<String, List<String>> userCommerciant = new HashMap<>();
        Map<String, Map<String, Integer>> userPaymentsCount = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction.getDescription().equals("Card payment") && !transaction.getCardHolder().equals(owner)) {
                String commerciant = transaction.getCommerciant();
                commerciantTotalReceived.put(commerciant,
                        commerciantTotalReceived.getOrDefault(commerciant, 0.0)
                                + (double) transaction.getAmount());
                String username = transaction.getCardHolder();
                userPaymentsCount.computeIfAbsent(commerciant, k -> new HashMap<>());
                Map<String, Integer> userPayments = userPaymentsCount.get(commerciant);
                userPayments.put(username, userPayments.getOrDefault(username, 0) + 1);
                userCommerciant.computeIfAbsent(commerciant, k -> new ArrayList<>()).add(username);
            }
        }
        List<CommerciantOutput> outputList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : userCommerciant.entrySet()) {
            List<String> users = entry.getValue();
            List<String> managers = getManagersUsername(users);
            List<String> employees = getEmployeesUsername(users);
            Map<String, Integer> userPayments = userPaymentsCount.get(entry.getKey());
            double totalReceived = commerciantTotalReceived.getOrDefault(entry.getKey(), 0.0);

            List<String> allManagers = getAllUserForCommerciantReport(managers, userPayments);
            List<String> allEmployees = getAllUserForCommerciantReport(employees, userPayments);

            Collections.sort(allManagers);
            Collections.sort(allEmployees);
            CommerciantOutput output = new CommerciantOutput(totalReceived, allManagers, allEmployees, entry.getKey());
            outputList.add(output);
        }
        outputList.sort((a, b) -> a.getCommerciant().compareTo(b.getCommerciant()));
        return outputList;
    }

    @Override
    public List<Transaction> getBusinessTransactionFiltered(final int startTimestamp,
                                                            final int endTimestamp) {
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : transactionsForBusiness) {
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    @Override
    public  List<String> getAllUserForCommerciantReport(List<String> users, Map<String, Integer> userPayments) {
        List<String> allUsers = new ArrayList<>();
        for (String manager : users) {
            int paymentCount = userPayments.getOrDefault(manager, 0);
            for (int i = 0; i < paymentCount; i++) {
                allUsers.add(manager);
            }
        }
        return allUsers;
    }

}
