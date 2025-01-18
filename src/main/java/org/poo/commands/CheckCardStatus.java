package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actionhandler.ErrorDescription;
import org.poo.actionhandler.ErrorOutput;
import org.poo.actionhandler.PrintOutput;
import org.poo.bank.BankDatabase;
import org.poo.bank.accounts.Account;
import org.poo.bank.cards.Card;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.transaction.TransactionBuilder;
import org.poo.transaction.TransactionDescription;

public class CheckCardStatus implements Commands {
    private final BankDatabase bank;
    private final CommandInput commandInput;
    private final ArrayNode output;

    public CheckCardStatus(final BankDatabase bank,
                           final CommandInput commandInput, final ArrayNode output) {
        this.bank = bank;
        this.commandInput = commandInput;
        this.output = output;
    }

    /**
     * method that check the status of the card
     */
    @Override
    public void execute() {
        Card card = bank.findCard(commandInput.getCardNumber());
        if (card == null) {
            ErrorOutput errorOutput = new ErrorOutput(ErrorDescription.CARD_NOT_FOUND.getMessage(),
                    commandInput.getTimestamp());
            ObjectNode node = errorOutput.toObjectNodeDescription();
            PrintOutput checkCardStatus = new PrintOutput("checkCardStatus", node,
                    commandInput.getTimestamp());
            checkCardStatus.printCommand(output);
            return;
        }
        Account account = card.getAccount();
        if (account.getMinAmount() >= account.getBalance() && card.getStatus().equals("active")) {
            card.setStatus("frozen");
            Transaction transaction = new TransactionBuilder(commandInput.getTimestamp(),
                    TransactionDescription.MINIMUM_FUNDS_REACHED.getMessage())
                    .build();
            account.addTransactionList(transaction);
        }
    }
}
