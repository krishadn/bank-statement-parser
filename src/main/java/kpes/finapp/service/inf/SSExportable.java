package kpes.finapp.service.inf;

import java.nio.file.Path;

/**
 * Interface for saving bank statements to spreadsheet
 * 
 * @author Krizzia Santillan
 */
public interface SSExportable {

    /**
     * Data types for spreadsheet cells
     */
    public enum DataType {
        STRING, DATE, AMOUNT
    }

    /**
     * Saves the bank statement to a spreadsheet file
     * 
     * @param p - the path to the spreadsheet file
     */
    void saveToSpreadSheet(Path p);

}
