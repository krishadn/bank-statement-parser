package kpes.finapp.service;

public class OldBankTransaction {

    private String description;
    private float amount;

    /**
     * Represents one transaction in a bank statement.
     * This can also be a beginning balance, transaction type summary, or ending balance for summarization
     * @param description describes the transation (can be beginning balance, ending balance, or transaction type summary)
     * @param amount amount of the transaction (or the summarized amount)
     */
    public OldBankTransaction(String description, float amount){
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
