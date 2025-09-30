package kpes.finapp.service;

public class BankTransaction {

    private String description;
    private float amount;


    public BankTransaction(String description, float amount){
        this.description = description;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public float getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f", description, amount);
    }
    
}
