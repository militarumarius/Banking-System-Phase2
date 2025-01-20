package org.poo.bank;

import lombok.Getter;
import org.poo.bank.accounts.Account;
import org.poo.bank.cards.Card;
import org.poo.fileio.*;
import org.poo.transaction.Commerciant;
import org.poo.transaction.SplitPaymentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.poo.utils.Utils.resetRandom;

public class BankDatabase {
    private final List<User> users;
    private final List<ExchangeRate> exchangeRates;
    private final Map<String, User> userMap = new HashMap<>();
    private final Map<String, Account> aliasMap = new HashMap<>();
    @Getter
    private final List<Commerciant> commerciants = new ArrayList<>();
    @Getter
    private final List<SplitPaymentTransaction> splitPayments = new ArrayList<>();

    public BankDatabase(final ObjectInput input) {
        resetRandom();
        users = new ArrayList<>();
        exchangeRates = new ArrayList<>();
        for (UserInput user : input.getUsers()) {
            users.add(new User(user));
        }
        for (ExchangeInput rate : input.getExchangeRates()) {
            addExchangeRate(rate);
        }
        for (CommerciantInput commerciant : input.getCommerciants()) {
            commerciants.add(new Commerciant(commerciant));
        }
        createEmailMap();
    }

    /**
     */
    public Map<String, User> getUserMap() {
        return userMap;
    }

    /**
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     */
    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    /**
     * method that copy the users for the output
     */
    public List<User> copyUsers() {
        List<User> copyUsers = new ArrayList<>();
        for (User user : users) {
            copyUsers.add(new User(user));
        }
        return copyUsers;
    }

    /**
     * method that create the email hasmap
     * */
    public void createEmailMap() {
        for (User user : users) {
            userMap.put(user.getEmail(), user);
        }
    }

    /**
     * method that add the exchange rate in the bank database
     */
    public void addExchangeRate(final ExchangeInput rate) {
        this.exchangeRates.add(new ExchangeRate(rate.getRate(), rate.getFrom(), rate.getTo()));
        double newRate = 1 / rate.getRate();
        this.exchangeRates.add(new ExchangeRate(newRate, rate.getTo(), rate.getFrom()));
    }

    /**
     * method that find the exchange rate from two currency , using a dfs recursive algorithm
     */
    public double findExchangeRate(final String from,
                                   final  String to,
                                   final List<String> visited) {
        if (from.equals(to)) {
            return 1;
        }
        visited.add(from);
        for (ExchangeRate rate : exchangeRates) {
            if (rate.getFrom().equals(from) && !visited.contains(rate.getTo())) {
                double partialRate = findExchangeRate(rate.getTo(), to, visited);
                if (partialRate != -1) {
                    return rate.getRate() * partialRate;
                }
            }
        }
        visited.remove(from);
        return -1;
    }

    /**
     * method that find the account by is iban
     */
    public Account findAccountByIban(final String iban) {
        for (User user : users) {
            Account account = user.findAccount(iban);
            if (account != null) {
                return account;
            }
        }
        return null;
    }

    /**
     * method that find the user by is iban
     */
    public User findUserByIban(final String iban) {
        for (User user : users) {
            Account account = user.findAccount(iban);
            if (account != null) {
                return user;
            }
        }
        return null;
    }

    /** */
    public Map<String, Account> getAliasMap() {
        return aliasMap;
    }

    /**
     * method that find a card by his cardnumber
     */
    public Card findCard(final String cardNumber) {
        for (User user : users) {
            Card card = user.findCard(cardNumber);
            if (card != null) {
                return card;
            }
        }
        return null;
    }

    /**
     * method that check if an acoount is an economy account
     */
    public boolean checkSaving(final Account account) {
        return account.getType().equals("savings");
    }

    /**
     * method that check if a payment can be split
     */
    public Account checkSplitPayment(final List<Account> accounts,
                                     final BankDatabase bank,
                                     final String currency,
                                     final List<Double> amountToPay) {
        for (int i =0; i < accounts.reversed().size(); i++) {
            List<String> visited = new ArrayList<>();
            double exchangeRate = bank.findExchangeRate(currency,
                    accounts.get(i).getCurrency(), visited);
            visited.clear();
            double amountToPayThisAccount = amountToPay.get(i) * exchangeRate;
            if (accounts.get(i).getBalance() < amountToPayThisAccount) {
                return accounts.get(i);
            }
        }
        return null;
    }

    /**
     * method that convert the iban list  to an account list
     */
    public List<Account> convertAccountfromString(final List<String> ibans) {
        List<Account> accounts = new ArrayList<>();
        for (String iban : ibans) {
            accounts.add(this.findAccountByIban(iban));
        }
        return accounts;
    }

    public Commerciant findCommerciant(String name){
        for (Commerciant commerciant : commerciants)
            if(commerciant.getCommerciant().equals(name))
                return commerciant;
        return null;
    }

    public SplitPaymentTransaction findSplitPaymentByUser(User user, String type){
        for (SplitPaymentTransaction splitPaymentTransaction : splitPayments) {
            for (Account account : splitPaymentTransaction.getAccountsNotAccept())
                if (user.findAccount(account.getIBAN()) != null && splitPaymentTransaction.getType().equals(type))
                    return splitPaymentTransaction;
        }
        return null;
    }
    public Account findAccountForSplitPayment(User user, String type) {
        for (SplitPaymentTransaction splitPaymentTransaction : splitPayments) {
            for (Account account : splitPaymentTransaction.getAccountsNotAccept())
                if (user.findAccount(account.getIBAN()) != null && splitPaymentTransaction.getType().equals(type))
                    return account;
        }
        return null;
    }

    public boolean checkCommerciantAccount(String iban){
        for (Commerciant commerciant : commerciants)
            if (commerciant.getAccount().equals(iban))
                return true;
        return false;
    }

    public Commerciant getCommerciantByIban(String iban){
        for (Commerciant commerciant : commerciants)
            if (commerciant.getAccount().equals(iban))
                return commerciant;
        return null;
    }
}
