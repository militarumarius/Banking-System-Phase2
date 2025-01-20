package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.User;
import org.poo.bank.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.transaction.SplitPaymentTransaction;

public class RejectSplitPayment implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public RejectSplitPayment(final BankDatabase bank,
                              final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    @Override
    public void execute() {
        User user = bank.getUserMap().get(commandInput.getEmail());
        if (user == null) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.
                    USER_NOT_FOUND.getMessage(), commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput rejectSplitPayment = new PrintOutput("rejectSplitPayment",
                    node, commandInput.getTimestamp());
            rejectSplitPayment.printCommand(output);
            return;
        }
        SplitPaymentTransaction splitPaymentTransaction = bank.findSplitPaymentByUser(user, commandInput.getSplitPaymentType());
        if (splitPaymentTransaction == null) {
            return;
        }
        splitPaymentTransaction.rejectTransaction(bank);
        bank.getSplitPayments().remove(splitPaymentTransaction);
    }
}
