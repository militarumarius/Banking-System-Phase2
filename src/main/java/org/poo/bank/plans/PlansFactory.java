package org.poo.bank.plans;

import org.poo.bank.accounts.Account;
import org.poo.bank.accounts.BasicAccount;
import org.poo.bank.accounts.EconomyAccount;
import org.poo.fileio.CommandInput;

import static org.poo.utils.Utils.generateIBAN;

public class PlansFactory {
    private PlansFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * method that create an account
     */
    public static Plan createPlan(String name) {

        switch (name) {
            case "silver" -> {
                return new SilverPlan();
            }
            case "gold" -> {
                return new GoldPlan();
            }
            default -> {
                return null;
            }
        }
    }
}
