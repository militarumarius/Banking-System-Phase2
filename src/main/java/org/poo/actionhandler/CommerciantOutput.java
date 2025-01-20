package org.poo.actionhandler;

import lombok.Getter;

import java.util.List;

@Getter
public class CommerciantOutput {
    private String commerciant;
    private double totalReceived;

    public CommerciantOutput(double totalReceived, List<String> managers, List<String> employees, String commerciant) {
        this.totalReceived = totalReceived;
        this.managers = managers;
        this.employees = employees;
        this.commerciant = commerciant;
    }

    private List<String> managers;
    private List<String> employees;
}
