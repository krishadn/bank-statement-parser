package kpes.finapp.service;

import java.time.LocalDate;

/**
 * Base class for savings account statements
 * @author Krizzia Santillan
 */
public abstract class SavingsStatement {

    // date fields
    protected LocalDate startDate;
    protected LocalDate endDate;

    public SavingsStatement() {
        super();
        startDate = LocalDate.now();
        endDate = LocalDate.now();
    }

}
