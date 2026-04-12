package kpes.finapp.service.base;

import java.time.LocalDate;

/**
 * Base class for savings account statements
 * 
 * @author Krizzia Santillan
 */
public abstract class SavingsStatement {

    /* Enums */

    /**
     * Enum for supported savings statement types
     */
    protected enum SavingsStatementType {
    }

    /* Fields */

    protected SavingsStatementType type;

    // date fields
    protected LocalDate startDate;
    protected LocalDate endDate;

    public SavingsStatement() {
        super();
        startDate = LocalDate.now();
        endDate = LocalDate.now();
        type = null;
    }

}
