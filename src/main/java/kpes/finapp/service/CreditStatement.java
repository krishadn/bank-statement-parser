package kpes.finapp.service;

import java.time.LocalDate;

/**
 * Base class for credit card statements
 * @author Krizzia Santillan
 */
public abstract class CreditStatement extends AbstractStatement {

    /* Fields */

    // date fields
    protected LocalDate statementDate;
    protected LocalDate dueDate;
    
    // other new fields
    protected double minAmountDue;

    /**
     * Constructor initializing field values
     */
    public CreditStatement(){
        super();
        statementDate = LocalDate.now();
        dueDate = LocalDate.now();
        minAmountDue = 0;
    }

    /* Getters */
    
    public LocalDate getStatementDate() {
        return statementDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public double getMinAmountDue() {
        return minAmountDue;
    }

    /* Concrete Methods */

    /**
     * Parses the {@link #rawString} and employs validity checks before 
     * populating the fields with parsed data. Updates parsed status to true
     * if all necessary fields are populated and validity checks are all successful.
     */
    @Override
    public void parseRawText() {
        preprocessRawText();

        /* dates + min due */
        extractStatementDate();
        extractDueDate();
        extractMinAmtDue();

        /* summary */
        extractPreviousBalance();
        extractTotalCredits();
        extractTotalDebits();
        extractTotalAmountDue();

        /* details */
        extractTransactionList();

        /* validate parsed data */
        if (isBalanced() && isTransactionComplete()) {
            parsed = true;
        } else {
            clearFields();
            throw new AssertionError("Bank Statement has erroneous data");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isBalanced() {
        return beginningBalance + totalCredits - totalDebits == endingBalance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isTransactionComplete() {
        double totalTransactions = transactions.stream()
                                                .mapToDouble(AbstractTransaction::getAmount)
                                                .sum();

        return totalTransactions == totalCredits - totalDebits;
    }
    
    private void clearFields() {
        beginningBalance = 0;
        totalCredits = 0;
        totalDebits = 0;
        endingBalance = 0;
        transactions.clear();
        parsed = false;

        statementDate = LocalDate.now();
        dueDate = LocalDate.now();
        minAmountDue = 0;
    }


    /* Abstract Methods */

    /**
     * Preprocesses the {@link #rawString} into a ready state before parsing.
     */
    protected abstract void preprocessRawText();

    /**
     * Extracts the Statement Date from the preprocessed {@link #rawString}
     * and assigns the extracted value to the {@link #statementDate} field.
     */
    protected abstract void extractStatementDate();

    /**
     * Extracts the Due Date from the preprocessed {@link #rawString}
     * and assigns the extracted value to the {@link #dueDate} field.
     */
    protected abstract void extractDueDate();
    
    /**
     * Extracts the Minimum Amount Due from the preprocessed {@link #rawString}
     * and assigns the extracted value to the {@link #minAmountDue} field.
     */
    protected abstract void extractMinAmtDue();

    protected abstract void extractPreviousBalance();
    protected abstract void extractTotalCredits();
    protected abstract void extractTotalDebits();
    protected abstract void extractTotalAmountDue();
    protected abstract void extractTransactionList();

}
