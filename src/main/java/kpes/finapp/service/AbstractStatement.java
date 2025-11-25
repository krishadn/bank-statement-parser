package kpes.finapp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Base class for bank statements
 * @author Krizzia Santillan
 */
public abstract class AbstractStatement implements SSExportable {

    // regular expression to use for checking if text extracted is from a valid bank statement
    protected Pattern pattern;
    
    // raw text
    protected String rawString;

    // parsing status
    protected boolean parsed;

    // summary
    protected double beginningBalance;
    protected double totalCredits;
    protected double totalDebits;
    protected double endingBalance;

    // details
    protected List<AbstractTransaction> transactions;

    /**
     * Constructor initializing field values
     */
    public AbstractStatement() {
        rawString = "";
        beginningBalance = 0;
        totalCredits = 0;
        endingBalance = 0;
        transactions = new ArrayList<>();
        pattern = setPattern();  
        parsed = false;
    }

    /* Concrete Methods */

    /**
     * Method to extract text from an ENCRYPTED bank statement, providing checks for validity, existence of file,
     * and file type. It also checks whether the pdf is a bank statement by checking against the {@code pattern}
     * (varies depending on specific {@link AbstractStatement} implementation)
     * @param path - path to the Bank Statement PDF file
     * @param extractor - {@link ITextExtractor} implementation
     * @param pw - password for decrypting encrypted Bank Statement PDF file 
     * @return true if the extraction is successful, false otherwise
     */
    public boolean extractStatementText(Path path, ITextExtractor extractor, String pw) {
        if (Files.notExists(path)) throw new IllegalArgumentException("File does not exist");
        if (!path.getFileName().toString().toLowerCase().endsWith(".pdf")) throw new IllegalArgumentException("File is not a PDF file");

        String result = "";

        try {
            result = extractor.extractText(path, pw);
            if (!pattern.matcher(result).find()) throw new IllegalArgumentException("File is not a Bank Statement");
            rawString = result;
            return true;

        } catch (IOException e) {
            System.out.println("Possible parsing error or decryption error");
            e.printStackTrace();
            return false;
        }

    }


    /**
     * Method for UNENCRYPTED bank statement. {@code pw} defaults to empty String
     * 
     * @see #extractStatementText(Path, ITextExtractor, String)
     */
    public boolean extractStatementText(Path path, ITextExtractor extractor) {
        return extractStatementText(path, extractor, "");
    }

    /* Abstract Methods */

    protected abstract Pattern setPattern();
    protected abstract void parseRawText();


}
