package kpes.finapp.service;

import java.time.LocalDate;

/**
 * Base class for credit card statements
 * @author Krizzia Santillan
 */
public abstract class CreditStatement extends AbstractStatement {

    // date fields
    protected LocalDate statementDate;
    protected LocalDate dueDate;
    
    // other new fields
    protected double minAmountDue;

    public CreditStatement(){
        super();
        statementDate = LocalDate.now();
        dueDate = LocalDate.now();
        minAmountDue = 0;
    }

}
