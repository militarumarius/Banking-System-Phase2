package org.poo.bank.accounts;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<User> users = new ArrayList<>();
    @JsonIgnore
    @Getter @Setter
    private double spendingLimits;
    @JsonIgnore
    @Getter @Setter
    private double depositLimits;
    @JsonIgnore
    private List<Transaction> transactionsForBusiness = new ArrayList<>();
    @JsonIgnore
    private double exchangeRate;
    @JsonIgnore
    private static final int AMOUNT = 500;

    /** */
    @Override
    public User getOwner() {
        return owner;
    }

    @JsonIgnore
    private User owner;

    /** */
    protected BusinessAccount(final BusinessAccount account) {
        super(account);
        this.users = account.getUsersList();
        this.owner = account.getOwner();
    }

    /** */
    protected BusinessAccount(final String iban,
                              final String type,
                              final String currency,
                              final String email,
                              final BankDatabase bank) {
        super(iban, type, currency);
        User user = bank.getUserMap().get(email);
        user.setRole("owner");
        this.owner = user;
        this.users.add(user);
        List<String> visited = new ArrayList<>();
        exchangeRate = bank.findExchangeRate("RON",
                currency, visited);
        spendingLimits = AMOUNT * exchangeRate;
        depositLimits = AMOUNT * exchangeRate;
    }

    /** */
    @Override
    public boolean isBusinessAccount() {
        return true;
    }

    /** */
    @Override @JsonIgnore
    public List<User> getUsersList() {
        return users;
    }

    /** */
    @Override
    public double getSpendingLimits() {
        return spendingLimits;
    }

    /** */
    @Override
    public double getDepositLimits() {
        return depositLimits;
    }

    /** */
    @Override
    public void setSpendingLimits(final double limit) {
        this.spendingLimits = limit;
    }


    /** */
    @Override
    public void setDepositLimits(final double limit) {
        this.depositLimits = limit;
    }

    /**
     * method that get the employees
     * */
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

    /**
     * method that get the managers
     * */
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

    /**
     * method that make a UserOutput list for JSON format
     * */
    private void makeUserOutputList(final List<UserOutput> managers,
                                    final Map<String, Double> totalSpent,
                                    final Map<String, Double> totalDeposit,
                                    final User user) {
        String username = user.getLastName() + " " + user.getFirstName();
        UserOutput userOutput;
        double sent = 0.0;
        double deposited = 0.0;
        if (totalDeposit.containsKey(username)) {
            deposited = totalDeposit.get(username);
        }
        if (totalSpent.containsKey(username)) {
            sent = totalSpent.get(username);
        }
        userOutput = new UserOutput(username, sent, deposited);
        managers.add(userOutput);
    }

    /** */
    @Override
    public List<Transaction> getTransactionsForBusiness() {
        return transactionsForBusiness;
    }

    /**
     * method that calculate the total sent money for every user
     * for business report
     * */
    @Override
    public Map<String, Double> calculateTotalSent(final List<Transaction> transactions) {
        Map<String, Double> userSpentMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String cardHolder = transaction.getCardHolder();
            double amount = (double) transaction.getAmount();
            String description = transaction.getDescription();
            if (description.equals("Card payment") && !transaction.getRole().equals("owner")) {
                userSpentMap.put(cardHolder,
                        userSpentMap.getOrDefault(cardHolder, 0.0) + amount);
            }
        }
        return userSpentMap;
    }

    /**
     * method that calculate the total deposited money for every user
     * for business report
     * */
    @Override
    public Map<String, Double> calculateTotalDeposited(final List<Transaction> transactions) {
        Map<String, Double> userDepositedMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            String cardHolder = transaction.getCardHolder();
            double amount = (double) transaction.getAmount();
            String description = transaction.getDescription();
            if (description.equals("Add funds") && !transaction.getRole().equals("owner")) {
                userDepositedMap.put(cardHolder,
                        userDepositedMap.getOrDefault(cardHolder, 0.0) + amount);
            }
        }
        return userDepositedMap;
    }

    /** */
    @Override
    public double totalSentForReport() {
        double total = 0.0;
        Map<String, Double> totalSpent = calculateTotalSent(transactionsForBusiness);
        for (Map.Entry<String, Double> entry : totalSpent.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    /** */
    @Override
    public double totalDepositForReport() {
        double total = 0.0;
        Map<String, Double> totalDeposit = calculateTotalDeposited(transactionsForBusiness);
        for (Map.Entry<String, Double> entry : totalDeposit.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    /** */
    @Override
    public boolean checkPaymentBusiness(final double amount,
                                        final String role) {
        if (role.equals("employee") && amount * 1 / exchangeRate > depositLimits) {
            return false;
        }
        return true;
    }

    /**
     * method used for business report commerciant type
     * */
    @Override
    public List<String> getManagersUsername(final List<String> usersnameList) {
        List<String> managers = new ArrayList<>();
        for (User user : users) {
            String username = user.getLastName() + " " + user.getFirstName();
            if (user.getRole().equals("manager") && usersnameList.contains(username)) {
                managers.add(username);
            }
        }
        return managers;
    }

    /**
     * method used for business report commerciant type
     * */
    @Override
    public List<String> getEmployeesUsername(final List<String> usersnameList) {
        List<String> employees = new ArrayList<>();
        for (User user : users) {
            String username = user.getLastName() + " " + user.getFirstName();
            if (user.getRole().equals("employee") && usersnameList.contains(username)) {
                employees.add(username);
            }
        }
        return employees;
    }

    /**
     * method that make the commerciants output object for the businees report
     * make a list of the object to respect the output format
     */
    @Override
    public List<CommerciantOutput> calculateCommerciants(final List<Transaction> transactions,
                                                         final String ownerUsername) {
        Map<String, Double> commerciantTotalReceived = new HashMap<>();
        Map<String, List<String>> userCommerciant = new HashMap<>();
        Map<String, Map<String, Integer>> userPaymentsCount = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction.getDescription().equals("Card payment")
                    && !transaction.getCardHolder().equals(ownerUsername)) {
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
        List<CommerciantOutput> CommerciantList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : userCommerciant.entrySet()) {
            List<String> usersList = entry.getValue();
            List<String> managers = getManagersUsername(usersList);
            List<String> employees = getEmployeesUsername(usersList);
            Map<String, Integer> userPayments = userPaymentsCount.get(entry.getKey());
            double totalReceived = commerciantTotalReceived.
                    getOrDefault(entry.getKey(), 0.0);

            List<String> allManagers = getAllUserForCommerciantReport(managers, userPayments);
            List<String> allEmployees = getAllUserForCommerciantReport(employees, userPayments);

            Collections.sort(allManagers);
            Collections.sort(allEmployees);
            CommerciantOutput output =
                    new CommerciantOutput(totalReceived, allManagers, allEmployees, entry.getKey());
            CommerciantList.add(output);
        }
        CommerciantList.sort((a, b) -> a.getCommerciant().compareTo(b.getCommerciant()));
        return CommerciantList;
    }

    /** */
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

    /** */
    @Override
    public  List<String> getAllUserForCommerciantReport(final List<String> usernameList,
                                                        final Map<String, Integer> userPayments) {
        List<String> allUsers = new ArrayList<>();
        for (String manager : usernameList) {
            int paymentCount = userPayments.getOrDefault(manager, 0);
            for (int i = 0; i < paymentCount; i++) {
                allUsers.add(manager);
            }
        }
        return allUsers;
    }

}
