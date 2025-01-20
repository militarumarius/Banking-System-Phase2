package org.poo.bank.accounts;

import org.poo.bank.BankDatabase;
import org.poo.fileio.CommandInput;

import static org.poo.utils.Utils.generateIBAN;

public final class FactoryAccount {

    private FactoryAccount() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * method that create an account
     */
    public static Account createAccount(final CommandInput input, final BankDatabase bank) {

        switch (input.getAccountType()) {
            case "classic" -> {
                return new BasicAccount(generateIBAN(),
                        input.getAccountType(), input.getCurrency());
            }
            case "savings" -> {
                return new EconomyAccount(generateIBAN(), input.getAccountType(),
                        input.getCurrency(), input.getInterestRate());
            }
            case "business" -> {
                return new BusinessAccount(generateIBAN(),
                        input.getAccountType(), input.getCurrency(),
                        input.getEmail(), bank);
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * methot that create an account for the copy constructor
     */
    public static Account createCopyAccount(final Account account) {
        switch (account.getType()) {
            case "classic" -> {
                return new BasicAccount((BasicAccount) account);
            }
            case "savings" -> {
                return new EconomyAccount((EconomyAccount) account);
            }
            case "business" -> {
                return new BusinessAccount((BusinessAccount) account);
            }
            default -> {
                return null;
            }
        }
    }
}




