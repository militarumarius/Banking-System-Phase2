package org.poo.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.bank.accounts.Account;
import org.poo.bank.accounts.FactoryAccount;
import org.poo.bank.cards.Card;
import org.poo.bank.plans.Plan;
import org.poo.bank.plans.StandardPlan;
import org.poo.bank.plans.StudentPlan;
import org.poo.fileio.UserInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    @JsonIgnore
    private final String birthDate;
    @JsonIgnore
    private final String occupation;
    private final List<Account> accounts;
    @JsonIgnore
    private final Map<String, Account> cardAccountMap = new HashMap<>();
    @JsonIgnore
    private Plan plan;
    @JsonIgnore @Getter @Setter
    private String role = "";
    @JsonIgnore @Getter @Setter
    private int numberOfPayments = 0;
    @JsonIgnore
    private static final int AMOUNT_THRESHOLD = 300;
    @JsonIgnore
    private static final int PAYMENTS_FOR_GOLD_UPGRADE = 5;

    public User(final UserInput user) {
        accounts = new ArrayList<>();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.birthDate = user.getBirthDate();
        this.occupation = user.getOccupation();
        this.plan = occupation.equals("student") ? new StudentPlan() : new StandardPlan();
    }

    /** */
    public String getLastName() {
        return lastName;
    }

    /** */
    public Plan getPlan() {
        return plan;
    }

    /** */
    public String getEmail() {
        return email;
    }

    /**
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /** */
    public String getBirthDate() {
        return birthDate;
    }

    /** */
    public String getOccupation() {
        return occupation;
    }

    /** */
    public void upgradePlan(final Plan newPlan) {
        this.plan = newPlan;
    }

    /** */
    public double calculateCommision(final double transactionAmount) {
        return plan.calculateFee(transactionAmount);
    }

    /**
     */
    @JsonIgnore
    public Map<String, Account> getCardAccountMap() {
        return cardAccountMap;
    }

    /**
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * copy constructor
     *
     * @param user the user that need to be copied
     */
    public User(final User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.occupation = user.getOccupation();
        this.birthDate = user.getBirthDate();
        this.plan = user.getPlan();
        this.accounts = new ArrayList<>();
        for (Account account : user.getAccounts()) {
            if (!account.isBusinessAccount()) {
                accounts.add(FactoryAccount.createCopyAccount(account));
            } else if (account.getOwner().equals(user)) {
                accounts.add(FactoryAccount.createCopyAccount(account));
            }
        }
    }

    /**
     * method that find an account by iban
     * @param iban the iban
     * @return the account find, or a null pointer
     */
    public Account findAccount(final String iban) {
        for (Account account : accounts) {
            if (account.getIBAN().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    /**
     * method that remove a card for the account
     * @param numberCard the number of the card
     * @return the account where the card was removed
     */
    public Account removeCard(final String numberCard) {
        for (Account account : accounts) {
            boolean check = account.getCards().
                    removeIf(card -> card.getCardNumber().equals(numberCard));
            if (check) {
                cardAccountMap.remove(numberCard);
                return account;
            }
        }
        return null;
    }

    /**
     * method that add a card to an account
     */
    public void addCard(final Account account, final Card card) {
        if (account == null) {
            return;
        }
        account.getCards().add(card);
        cardAccountMap.putIfAbsent(card.getCardNumber(), account);
    }

    /** */
    public void addCardforBusiness(final Account account, final Card card) {
        cardAccountMap.put(card.getCardNumber(), account);
    }

    /**
     * method that find a card by is number
     *
     * @param cardNumber the card number that need to be found
     */
    public Card findCard(final String cardNumber) {
        for (Account account : accounts) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return card;
                }
            }
        }
        return null;
    }

    /**
     * function that add an account to the user
     */
    public void addAccount(final Account account) {
        this.accounts.add(account);
    }

    /** */
    public Account findAccountForWithdrawSavings(final String currency) {
        for (Account account : accounts) {
            if (!account.isBusinessAccount()
                    && account.getType().equals("classic")
                    && account.getCurrency().equals(currency)) {
                return account;
            }
        }
        return null;
    }

    /** */
    public boolean userCheckUpgradePlan(final String newPlan) {
        if (plan.getName().equals("gold")) {
            return false;
        }
        if (plan.getName().equals("silver")
                && (newPlan.equals("student")
                || newPlan.equals("standard"))) {
            return false;
        }
        return true;
    }

    /** */
    public boolean checkUpgradeGoldPlan(final double amount) {
        if (plan.getName().equals("silver") && amount >= AMOUNT_THRESHOLD) {
            numberOfPayments++;
        }
        if (numberOfPayments == PAYMENTS_FOR_GOLD_UPGRADE
                && plan.getName().equals("silver")) {
            return true;
        }
        return false;
    }
}
