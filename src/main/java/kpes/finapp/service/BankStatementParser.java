package kpes.finapp.service;

import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * @author Krizzia Santillan
 */
public class BankStatementParser {

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
     * @return String with only the list of transactions
     */
    public static String getData(String fullText, Bank bank) {
        assert !(fullText.isBlank() || fullText.isEmpty()) : "Text argument is empty/blank";
        
        String delimiter = getDelimiter(bank);
        assert fullText.contains(delimiter): "Invalid bank statement";

        int txnIndex = getDataIndex(bank);

        return fullText.split(delimiter)[txnIndex].trim().replaceAll(" ", "");
    }


    /**
     * To set up a default parameter value for Bank in {@link #getData(String, Bank)}
     * @param fullText text that comes from using {@link #extractText(Path)}
     * @return String with only the list of transactions
     */
    public static String getData(String fullText) {
        return getData(fullText, Bank.BPICC);
    }

    
    /**
     * To define the delimiter for splitting full PDF text from the bank statement 
     * to extract data depending on the bank and type of account
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
     * To define the index of the data in the array of Strings 
     * resulting from splitting full PDF text from the bank statement
     * @param bank any of the supported bank statements {@link #Bank}
     * @return the index of transaction listing
     */
    private static int getDataIndex(Bank bank){
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


    /**
     * Create a {@link BankStatement} object representation for the bank statement
     * @param data clean text data from parsed bank statement pdf
     * @param bank any of the supported bank statements {@link #Bank}
     * @return {@link BankStatement}
     */
    public static BankStatement processData(String data, Bank bank){
        switch (bank) {
            case BPICC:
                return processBPICC(data);
            case GCASH:
                // TODO next iteration
                return null;                      
            default:
                return null;
        }
    }


    /**
     * Create a {@link BankStatement} object representation for BPICC bank statement
     * @param data clean text data from parsed bank statement pdf 
     * @return {@link BankStatement}
     */
    private static BankStatement processBPICC(String data) {
        String[] dataParts = splitBPICC(data);
        List<BankTransaction> summary = getSummaryBPICC(dataParts[1]);
        List<BankTransaction> details = getDetailsBPICC(dataParts[2]);
        BankStatement bs = new BankStatement(Bank.BPICC, summary, details);
        return bs;
    }


    /**
     * Splits the data into its constituent parts using account number as delimiter
     * @param data clean text from the bank statement 
     * @return data split into (0) header, (1) summary, (2) transaction listing
     */
    private static String[] splitBPICC(String data) {
        return data.split("\\d{6}-\\d{1}-\\d{2}-\\d{7}");
    }


    /**
     * Parse summary data to create a {@link BankTransaction} objects
     * and store the objects in a List
     * @param summaryData summary of the month's transactions
     * @return list of {@link BankTransaction}
     */
    private static ArrayList<BankTransaction> getSummaryBPICC(String summaryData) {

        ArrayList<BankTransaction> summary = new ArrayList<BankTransaction>();

        String txnRegex = "(.*\\D{2})(-?(\\d{1,3},)*\\d{1,3}\\.\\d{2})";
        Pattern p = Pattern.compile(txnRegex);
        Matcher matcher = p.matcher(summaryData);

        while (matcher.find()){
            String desc = matcher.group(1);

             try {
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                Number parsedAmount = format.parse(matcher.group(2));
                float amount = parsedAmount.floatValue();
                BankTransaction txn = new BankTransaction(desc, amount);
                summary.add(txn);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return summary;
    }


    /**
     * Parse transaction details to create a {@link BankTransaction} objects
     * and store the objects in a List
     * @param detailsData transaction listing which may include SIP details
     * @return list of {@link BankTransaction}
     * Note: there is no support yet for SIP details
     */
    private static ArrayList<BankTransaction> getDetailsBPICC(String detailsData) {

        ArrayList<BankTransaction> details = new ArrayList<BankTransaction>();
        String[] parts = detailsData.split("S.I.P.BALANCESUMMARY");

        String txnRegex = "(.*\\D{2}(\\d{2}/\\d{2})?)(-?(\\d{1,3},)*\\d{1,3}\\.\\d{2})";
        Pattern p = Pattern.compile(txnRegex);
        Matcher matcher = p.matcher(parts[0]);

        while (matcher.find()){
            String desc = matcher.group(1);

             try {
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                Number parsedAmount = format.parse(matcher.group(3));
                float amount = parsedAmount.floatValue();
                BankTransaction txn = new BankTransaction(desc, amount);
                details.add(txn);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return details;
    }






    
}
