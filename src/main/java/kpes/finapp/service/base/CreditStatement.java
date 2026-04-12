package kpes.finapp.service.base;

import java.time.LocalDate;

/**
 * Base class for credit card statements
 * 
 * @author Krizzia Santillan
 */
public abstract class CreditStatement extends AbstractStatement {

    /* Enums */

    /**
     * Enum for supported credit statement types
     */
    public enum CreditStatementType {
        BPICC
    }

    /* Constants */
    private static final double EPSILON = 0.000001;

    /* Fields */

    protected CreditStatementType type;

    // date fields
    protected LocalDate statementDate;
    protected LocalDate dueDate;

    // summary fields
    protected double minAmountDue;

    /**
     * Constructor initializing field values
     */
    public CreditStatement() {
        super();
        statementDate = null;
        dueDate = null;
        minAmountDue = 0;
        type = null;
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

    public CreditStatementType getType() {
        return type;
    }

    /* Concrete Methods */

    /**
     * Parses the {@link #rawString} and employs validity checks before populating
     * the fields with parsed data. Updates parsed status to true if all necessary
     * fields are populated and validity checks are all successful.
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
        return Math.abs(beginningBalance + totalCredits - totalDebits - endingBalance) < EPSILON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isTransactionComplete() {
        double totalTransactions = transactions.stream().mapToDouble(AbstractTransaction::getAmount).sum();
        return Math.abs(totalTransactions - (totalCredits - totalDebits)) < EPSILON;
    }

    private void clearFields() {
        beginningBalance = 0;
        totalCredits = 0;
        totalDebits = 0;
        endingBalance = 0;
        transactions.clear();
        parsed = false;

        statementDate = null;
        dueDate = null;
        minAmountDue = 0;
        type = null;
    }

    /* Abstract Methods */

    /**
     * Preprocesses the {@link #rawString} into a ready state before parsing.
     */
    protected abstract void preprocessRawText();

    /**
     * Extracts the Statement Date from the preprocessed {@link #rawString} and
     * assigns the extracted value to the {@link #statementDate} field.
     */
    protected abstract void extractStatementDate();

    /**
     * Extracts the Due Date from the preprocessed {@link #rawString} and assigns
     * the extracted value to the {@link #dueDate} field.
     */
    protected abstract void extractDueDate();

    /**
     * Extracts the Minimum Amount Due from the preprocessed {@link #rawString} and
     * assigns the extracted value to the {@link #minAmountDue} field.
     */
    protected abstract void extractMinAmtDue();

    /**
     * Extracts the Previous Balance from the preprocessed {@link #rawString} and
     * assigns the extracted value to the {@link #beginningBalance} field.
     */
    protected abstract void extractPreviousBalance();

    /**
     * Extracts the Total Credits from the preprocessed {@link #rawString} and
     * assigns the extracted value to the {@link #totalCredits} field.
     */
    protected abstract void extractTotalCredits();

    /**
     * Extracts the Total Debits from the preprocessed {@link #rawString} and
     * assigns the extracted value to the {@link #totalDebits} field.
     */
    protected abstract void extractTotalDebits();

    /**
     * Extracts the Total Amount Due from the preprocessed {@link #rawString} and
     * assigns the extracted value to the {@link #endingBalance} field.
     */
    protected abstract void extractTotalAmountDue();

    /**
     * Extracts the Transaction List from the preprocessed {@link #rawString} and
     * adds extracted transactions to the {@link #transactions} field.
     */
    protected abstract void extractTransactionList();

}
