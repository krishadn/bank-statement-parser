package kpes.finapp.service;

import java.time.LocalDate;

public class SavingsTransaction extends AbstractTransaction {
    
    private String referenceNum;

    public SavingsTransaction(LocalDate transactionDate, 
                                    String description,
                                    float amount,
                                    String referenceNum) {

        super(transactionDate, description, amount);
        this.referenceNum = referenceNum;
        
    }

    public String getReferenceNum() {
        return referenceNum;
    }

    public void setReferenceNum(String referenceNum) {
        this.referenceNum = referenceNum;
    }

    @Override
    public String toString() {
        return String.format("%tD - %s - %s - %,.2f", transactionDate, referenceNum, description, amount);
    }

}
