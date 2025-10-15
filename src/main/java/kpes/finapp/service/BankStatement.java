package kpes.finapp.service;

import java.time.LocalDate;
import java.util.List;

import kpes.finapp.service.BankStatementParser.Bank;

public class BankStatement {
    
    private Bank bankAndType;
    private List<BankTransaction> summary;
    private List<BankTransaction> details;
    private float totalTxnAmount;
    private LocalDate statementDate;
    private LocalDate dueDate;


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

        statementDate = null;
        dueDate = null;
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

    /**
     * To get the statement date, if any
     * @return {@link LocalDate} for bank types with statement date; null for bank types with no statement date
     */
    public LocalDate getStatementDate() {
        return statementDate;
    }
    
    public void setStatementDate(LocalDate statementDate) {
        this.statementDate = statementDate;
    }

    /**
     * To get the due date, if any
     * @return {@link LocalDate} for bank types with due date; null for bank types with no due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * To get the amount due, if any
     * @return amount due
     */
    public float getAmountDue(){
        if (bankAndType.equals(Bank.BPICC)) {

            for (BankTransaction txn: summary) {
                if (txn.getDescription().equals("EndingBalance")){
                    return txn.getAmount();
                }
            }

        }

        return 0;
    }

    @Override
    public String toString() {
        if (bankAndType.equals(Bank.BPICC)) {
            return String.format("Statement Date: %tB %<td, %<tY \nDue Date: %tB %<td, %<tY \nTotal Transactions: %,.2f \nAmount Due: %,.2f", 
                                    statementDate, dueDate, totalTxnAmount,getAmountDue());
        }
        
        // TODO for other types
        return "";
    }

}
