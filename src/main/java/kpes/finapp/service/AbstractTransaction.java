package kpes.finapp.service;

import java.time.LocalDate;

/**
 * Base class for bank transactions
 * @author Krizzia Santillan
 */
public abstract class AbstractTransaction {

    protected LocalDate transactionDate;
    protected String description;
    protected float amount;

    // constructor
    protected AbstractTransaction(LocalDate transactionDate, 
                                    String description,
                                    float amount) {
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;

    }

    // getters and setters
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public float getAmount() {
        return amount;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }


    // abstract method/s
    @Override
    public abstract String toString();

    
}
