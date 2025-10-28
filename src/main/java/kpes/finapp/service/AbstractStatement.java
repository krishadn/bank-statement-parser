package kpes.finapp.service;

import java.util.List;

/**
 * Base class for bank statements
 * @author Krizzia Santillan
 */
public abstract class AbstractStatement {
    
    // summary
    protected float beginningBalance;
    protected float totalCredits;
    protected float totalDebits;
    protected float endingBalance;

    // details
    protected List<AbstractTransaction> transactions;

    // constructor
    protected AbstractStatement(float beginningBalance, float totalCredits, float totalDebits,
                                    float endingBalance, List<AbstractTransaction> transactions) {

        boolean matchFlag = doTransactionsMatchSummary(beginningBalance, totalCredits, totalDebits, endingBalance, transactions);

        if (!matchFlag) throw new IllegalArgumentException("Debits and credits in transactions have discrepancies with summary");

        this.beginningBalance = beginningBalance;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
        this.endingBalance = endingBalance;
        this.transactions = transactions;

    }

    // getters
    public float getBeginningBalance() {
        return beginningBalance;
    }

    public float getTotalCredits() {
        return totalCredits;
    }

    public float getTotalDebits() {
        return totalDebits;
    }

    public float getEndingBalance() {
        return endingBalance;
    }

    public List<AbstractTransaction> getTransactions() {
        return transactions;
    }

    // abstract methods
    @Override
    public abstract String toString();
    protected abstract Boolean doTransactionsMatchSummary(float beginningBalance, float totalCredits, float totalDebits,
                                                                float endingBalance, List<AbstractTransaction> transactions);

}
