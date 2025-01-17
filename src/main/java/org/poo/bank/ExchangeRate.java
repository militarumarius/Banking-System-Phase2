package org.poo.bank;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExchangeRate {
    private String from;
    private String to;
    private double rate;

    public ExchangeRate(final double rate,
                        final String from,
                        final String to) {
        this.rate = rate;
        this.from = from;
        this.to = to;
    }

}
