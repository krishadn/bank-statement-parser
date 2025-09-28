package kpes.finapp.service;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * @author Krizzia Santillan
 */
public class BankStatementParserService {

    /**
     * Supported bank statements
     */
    public enum Bank {
        BPICC,
        GCASH
    }


    /**
     * Method to extract text from a PDF file using {@link PDFTextStripper}
     * @param filePath instance of path that points to the PDF document
     * @return String text of the PDF document
     */
    public static String extractText(Path filePath) {
        String text = "";

        try {
            PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(filePath));
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            text = pdfStripper.getText(document).trim();
        } catch (IOException e) {
            // TODO: handle exceptions
            e.printStackTrace();
        }
        
        return text;
    }


    /**
     * Removes all text from an extracted PDF text that are not part of the list of transactions
     * @param fullText non-empty String that comes from using {@link #extractText(Path)}
     * @param bank any of the supported bank statements {@link #Bank}
     * @return 
     */
    public static String getTransactions(String fullText, Bank bank) {
        assert !(fullText.isBlank() || fullText.isEmpty());
        String delimiter = getDelimiter(bank);
        int txnIndex = getTxnListIndex(bank);
        return fullText.split(delimiter)[txnIndex];
    }


    /**
     * To set up a default parameter value for Bank in {@link #getTransactions(String, Bank)}
     * @param fullText text that comes from using {@link #extractText(Path)}
     * @return
     */
    public static String getTransactions(String fullText) {
        return getTransactions(fullText, Bank.BPICC);
    }


    /**
     * To define the delimiter for splitting full PDF text from the bank statement 
     * to extract transaction listing depending on the bank and type of account
     * @param bank any of the supported bank statements {@link #Bank}
     * @return the delimiter to use
     */
    private static String getDelimiter(Bank bank){
        String delimiter = "";

        switch (bank) {
            case BPICC:
                delimiter = "Statement of Account";
                break;
            case GCASH:
                // TODO: for next iteration

                break;
            default:
                break;
        }

        return delimiter;
    }


    /**
     * To define the index of the transaction list in the array of Strings 
     * resulting from splitting full PDF text from the bank statement
     * @param bank any of the supported bank statements {@link #Bank}
     * @return the index of transaction listing
     */
    private static int getTxnListIndex(Bank bank){
        int index = -1;

        switch (bank) {
            case BPICC:
                index = 2;
                break;
            case GCASH:
                // TODO: for next iteration

                break;
            default:
                break;
        }

        return index;
    }


    
}
