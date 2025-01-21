package org.poo.actionhandler;

import lombok.Getter;

@Getter
public class UserOutput {
    private final String username;
    private final double spent;
    private final double deposited;

    public UserOutput(
            final String username,
            final double spent,
            final double deposited) {
        this.username = username;
        this.spent = spent;
        this.deposited = deposited;
    }

}
