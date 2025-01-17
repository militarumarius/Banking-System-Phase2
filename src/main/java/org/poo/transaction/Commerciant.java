package org.poo.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Commerciant {
    private String commerciant;
    private double total;

    public Commerciant(final String commerciant, final double total) {
        this.commerciant = commerciant;
        this.total = total;
    }
}
