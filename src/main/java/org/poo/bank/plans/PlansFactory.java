package org.poo.bank.plans;

public final class PlansFactory {
    /** */
    private PlansFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * method that create a plan
     */
    public static Plan createPlan(final String name) {

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
