package kpes.finapp.service;

import java.util.List;

import kpes.finapp.service.BankStatementParser.Bank;

public class BankStatement {
    
    private Bank bankAndType;
    private List<BankTransaction> summary;
    private List<BankTransaction> details;
    private float totalTxnAmount;

    public BankStatement(Bank bankAndType, 
                            List<BankTransaction> summary, 
                            List<BankTransaction> details) {

        this.bankAndType = bankAndType;
        
        this.summary = summary;
        this.details = details;
        
        totalTxnAmount = 0;
        for (BankTransaction txn: this.details){
            totalTxnAmount += txn.getAmount();
        }
    }

    public Bank getBankAndType() {
        return bankAndType;
    }

    public List<BankTransaction> getSummary() {
        return summary;
    }

    public List<BankTransaction> getDetails() {
        return details;
    }

    public float getTotalTxnAmount() {
        return totalTxnAmount;
    }


}
