package org.poo.actionhandler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserOutput {
    final String username;
    final double spent;
    final double deposited;

    public UserOutput(
            final String username,
            final double spent,
            final double deposited) {
        this.username = username;
        this.spent = spent;
        this.deposited = deposited;
    }

}
