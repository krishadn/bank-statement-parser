package kpes.finapp.service;

import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private enum DateType {
        DUE,
        STATEMENT
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
            text = "Invalid path";
        }
        
        return text;
    }

    /**
     * To get the statement date of the bank statement
     * @param fullText non-empty String that comes from using {@link #extractText(Path)}
     * @param bank any of the supported bank statements {@link #Bank}
     * @return {@link LocalDate} representing the statement date
     */
    public static LocalDate parseStatementDate(String fullText, Bank bank) {
        String dateString = "";
        String pattern = "";
        switch (bank) {
            case BPICC:
                dateString = parseDateBPI(fullText, DateType.STATEMENT);
                pattern = "MMMMdd,yyyy";
            case GCASH:
                // TODO: for next iteration

                break;
            default:
                break;
        }

        return formatDate(dateString, pattern);

    }

    /**
     * To set up a default parameter value for Bank in {@link #parseStatementDate(String, Bank)}
     * @param fullText text that comes from using {@link #extractText(Path)}
     * @return {@link LocalDate} representing the statement date
     */
    public static LocalDate parseStatementDate(String fullText) {
        return parseStatementDate(fullText, Bank.BPICC);
    }

    /**
     * To get the due date of the bank statement
     * @param fullText non-empty String that comes from using {@link #extractText(Path)}
     * @param bank any of the supported bank statements {@link #Bank}
     * @return {@link LocalDate} representing the due date
     */
    public static LocalDate parseDueDate(String fullText, Bank bank) {
        String dateString = "";
        String pattern = "";
        switch (bank) {
            case BPICC:
                dateString = parseDateBPI(fullText, DateType.DUE);
                pattern = "MMMMdd,yyyy";
            case GCASH:
                // TODO: for next iteration

                break;
            default:
                break;
        }

        return formatDate(dateString, pattern);
        
    }

    /**
     * To set up a default parameter value for Bank in {@link #parseDueDate(String, Bank)}
     * @param fullText text that comes from using {@link #extractText(Path)}
     * @return {@link LocalDate} representing the due date
     */
    public static LocalDate parseDueDate(String fullText) {
        return parseDueDate(fullText, Bank.BPICC);
    }

    /**
     * Convert String into a LocalDate object
     * @param dateString date to be converted
     * @param pattern format of the dateString
     * @return {@link LocalDate} representing the date
     */
    private static LocalDate formatDate(String dateString, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate date = LocalDate.parse(dateString, formatter);
            return date;
        }
        catch (DateTimeParseException e) {
            e.printStackTrace();
            // TODO handle exception
        }

        return null;
    }

    /**
     * Parses the text to get the statement or due date in this format: MMMMdd,yyyy
     * @param fullText text that comes from using {@link #extractText(Path)}
     * @return date String in MMMMdd,yyyy format
     */
    private static String parseDateBPI(String fullText, DateType dt) {

        String dateRegex = "";

        if (dt.equals(DateType.STATEMENT)){
            dateRegex = "S T A T E M E N T D A T E ([\\w\\s]*\\d \\d? ?, 2 0 \\d \\d)";
        } else if (dt.equals(DateType.DUE)) {
            dateRegex = "P A Y M E N T D U E D A T E ([\\w\\s]*\\d \\d? ?, 2 0 \\d \\d)";
        }

        Pattern p = Pattern.compile(dateRegex);
        Matcher matcher = p.matcher(fullText);

        if (matcher.find()){
            String[] rawDate = matcher.group(1).split(" ");
            int digitCount = 0;

            for (int i=1; i < rawDate.length; i++) {
                if (rawDate[i].matches("\\d")) {
                    digitCount++;
                }
                rawDate[i] = rawDate[i].toLowerCase();
            }

            if (digitCount < 6) {
                int insertionIndex = String.join("", rawDate).indexOf(",") - 1;
                String[] leftPart = Arrays.copyOfRange(rawDate, 0, insertionIndex);
                String[] rightPart = Arrays.copyOfRange(rawDate, insertionIndex, rawDate.length);
                List<String> fullDate = new ArrayList<>();
                Collections.addAll(fullDate, leftPart);
                fullDate.add("0");
                Collections.addAll(fullDate, rightPart);
                return String.join("", fullDate);

            }
            
            return String.join("", rawDate);
        }

        return "";

    }


    /**
     * Removes all text from an extracted PDF text that are not part of the list of transactions
     * @param fullText non-empty String that comes from using {@link #extractText(Path)}
     * @param bank any of the supported bank statements {@link #Bank}
     * @return String with only the relevant data
     */
    public static String parseData(String fullText, Bank bank) {
        assert !(fullText.isBlank() || fullText.isEmpty()) : "Text argument is empty/blank";
        
        String delimiter = getDelimiter(bank);
        assert fullText.contains(delimiter): "Bank statement format not supported. Check format updates";

        int txnIndex = getDataIndex(bank);

        String data = fullText.split(delimiter)[txnIndex].trim();
        
        if (bank == Bank.BPICC) {
            data = data.replaceAll(" ", "");
        }

        return data;
    }


    /**
     * To set up a default parameter value for Bank in {@link #parseData(String, Bank)}
     * @param fullText text that comes from using {@link #extractText(Path)}
     * @return String with only the list of transactions
     */
    public static String parseData(String fullText) {
        return parseData(fullText, Bank.BPICC);
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
        List<BankTransaction> summary = parseSummaryBPICC(dataParts[1]);

        List<BankTransaction> details;

        // if there is no transactions, array will only have 2 elements
        if (dataParts.length == 3) {
            details = parseDetailsBPICC(dataParts[2]);
        } else {
            details = new ArrayList<BankTransaction>();
        }

        BankStatement bs = new BankStatement(Bank.BPICC, summary, details);
        return bs;
    }


    /**
     * Splits the data into its constituent parts using account number as delimiter
     * @param data clean text from the bank statement 
     * @return data split into (0) header, (1) summary, (2) transaction listing
     * @throws IllegalArgumentException when the text cannot be split using account number
     */
    private static String[] splitBPICC(String data) {
        String acctNum = "\\d{6}-\\d{1}-\\d{2}-\\d{7}";
        Pattern p = Pattern.compile(acctNum);
        Matcher m = p.matcher(data);

        if (m.find()) {
            return p.split(data);
        } else {
            throw new IllegalArgumentException("Cannot split argument using existing regex. Check format update");
        }
    }


    /**
     * Parse summary data to create a {@link BankTransaction} objects
     * and store the objects in a List
     * @param summaryData summary of the month's transactions
     * @return list of {@link BankTransaction}
     */
    private static ArrayList<BankTransaction> parseSummaryBPICC(String summaryData) {

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

        if (summary.isEmpty()) {
            throw new IllegalStateException("Did not find match using current regex");
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
    private static ArrayList<BankTransaction> parseDetailsBPICC(String detailsData) {

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

        if (details.isEmpty()) {
            throw new IllegalStateException("Did not find match using current regex");
        }

        return details;
    }


    public static BankStatement createDatedBankStatement(Path filePath, Bank bank) {

        String fullText = extractText(filePath);
        LocalDate statementDate = parseStatementDate(fullText, bank);
        LocalDate dueDate = parseDueDate(fullText, bank);
        
        String transactionData = parseData(fullText, bank);
        BankStatement bs = processData(transactionData, bank);

        bs.setStatementDate(statementDate);
        bs.setDueDate(dueDate);

        return bs;
    }


    public static void saveBankStatementToExcel(BankStatement bs){

        // TODO

    }



    
}
