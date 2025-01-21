package org.poo.actionhandler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class CommerciantOutput {
    private String commerciant;
    @JsonProperty("total received")
    private double totalReceived;

    public CommerciantOutput(final double totalReceived,
                             final List<String> managers,
                             final List<String> employees,
                             final String commerciant) {
        this.totalReceived = totalReceived;
        this.managers = managers;
        this.employees = employees;
        this.commerciant = commerciant;
    }

    private List<String> managers;
    private List<String> employees;
}
