package kpes.finapp.service;

import java.time.LocalDate;

/**
 * Base class for bank transactions
 * @author Krizzia Santillan
 */
public abstract class AbstractTransaction {

    protected LocalDate transactionDate;
    protected String description;
    protected double amount;

    // constructor
    protected AbstractTransaction(LocalDate transactionDate, 
                                    String description,
                                    double amount) {
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;

    }

    // getters
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    // abstract method/s
    @Override
    public abstract String toString();

    
}
