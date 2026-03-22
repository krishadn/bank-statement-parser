package kpes.finapp.service.txns;

import java.time.LocalDate;

import kpes.finapp.service.base.AbstractTransaction;

public class SavingsTransaction extends AbstractTransaction {
    
    private String referenceNum;

    public SavingsTransaction(LocalDate transactionDate, 
                                    String description,
                                    double amount,
                                    String referenceNum) {

        super(transactionDate, description, amount);
        this.referenceNum = referenceNum;
        
    }

    public String getReferenceNum() {
        return referenceNum;
    }

    @Override
    public String toString() {
        return String.format("%tD - %s - %s - %,.2f", transactionDate, referenceNum, description, amount);
    }

}
