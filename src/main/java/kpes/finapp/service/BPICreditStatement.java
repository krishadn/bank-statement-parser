package kpes.finapp.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BPICreditStatement extends CreditStatement {

    private double unbilledInstallmentAmt;
    private List<AbstractTransaction> installmentTxns;
    private int points;


    public BPICreditStatement() {
        super();
        unbilledInstallmentAmt = 0;
        installmentTxns = new ArrayList<>();
        points = 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Pattern createPattern() {
        return Pattern.compile("Statement of Account");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preprocessRawText() {
        rawString = rawString.replaceAll(" ", "");
    }

    /**
     * {@inheritDoc}
     * Uses additional function {@link #formatDate(String)} to fix extracted
     * date format before converting it to {@link LocalDate} type.
     * @throws IllegalStateException when the pattern for Statement Date is not found
     */
    @Override
    protected void extractStatementDate() {

        String dateRegex = "STATEMENTDATE([A-Z]{3,9}\\d\\d?,20\\d\\d)";
        Pattern p = Pattern.compile(dateRegex);
        Matcher matcher = p.matcher(rawString);

        if (matcher.find()) {
            String rawDate = matcher.group(1);
            statementDate = formatDate(rawDate);
            return;
        }

        throw new IllegalStateException("Cannot find Statement Date from extracted text. Check updates in statement format");

    }

    /**
     * {@inheritDoc}
     * Uses additional function {@link #formatDate(String)} to fix extracted
     * date format before converting it to {@link LocalDate} type.
     * @throws IllegalStateException when the pattern for Due Date is not found
     */
    @Override
    protected void extractDueDate() {

        String dateRegex = "PAYMENTDUEDATE([A-Z]{3,9}\\d\\d?,20\\d\\d)";
        Pattern p = Pattern.compile(dateRegex);
        Matcher matcher = p.matcher(rawString);

        if (matcher.find()) {
            String rawDate = matcher.group(1);
            dueDate = formatDate(rawDate);
            return;
        }

        throw new IllegalStateException("Cannot find Due Date from extracted text. Check updates in statement format");
    }

    private LocalDate formatDate(String rawDate) {

        // convert into MMMM format (i.e from OCTOBER to October)
        Pattern digit = Pattern.compile("\\d");
        Matcher digitMatcher = digit.matcher(rawDate);

        digitMatcher.find();
        String month = rawDate.substring(1, digitMatcher.start());
        month = rawDate.charAt(0) + month.toLowerCase();
        
        String dateYear = rawDate.substring(digitMatcher.start());
 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMMd,yyyy");
        String formattedDate = month + dateYear;        
        return LocalDate.parse(formattedDate, formatter);
    }    

    /**
     * {@inheritDoc}
     * @throws IllegalStateException when the pattern for Minimum Amount Due is not found 
     */
    @Override
    protected void extractMinAmtDue() {
        String minAmtDueRegex = "MINIMUMAMOUNTDUE((\\d{1,3},)*\\d{1,3}\\.\\d\\d)";
        Pattern p = Pattern.compile(minAmtDueRegex);
        Matcher matcher = p.matcher(rawString);

        if (matcher.find()) {
            minAmountDue = parseAmount(matcher.group(1));
            return;
        }

        throw new IllegalStateException("Cannot find Minimum Amount Due from extracted text. Check updates in statement format");

    }

    private double parseAmount(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###.###");
        try {
            return formatter.parse(amount).doubleValue();
        } catch (ParseException e) {
            System.out.println("Parsing error");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException when the pattern for Previous Balance is not found 
     */
    @Override
    protected void extractPreviousBalance() {
        String prevBalRegex = "PreviousBalance((\\d{1,3},)*\\d{1,3}\\.\\d\\d)";
        Pattern p = Pattern.compile(prevBalRegex);
        Matcher matcher = p.matcher(rawString);

        if (matcher.find()) {
            beginningBalance = parseAmount(matcher.group(1));
            return;
        }

        throw new IllegalStateException("Cannot find Previous Balance from extracted text. Check updates in statement format");
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException when the pattern for Total Credits is not found 
     */
    @Override
    protected void extractTotalCredits() {
        String creditsRegex = "Total((\\d{1,3},)*\\d{1,3}\\.\\d\\d)((\\d{1,3},)*\\d{1,3}\\.\\d\\d)((\\d{1,3},)*\\d{1,3}\\.\\d\\d)((\\d{1,3},)*\\d{1,3}\\.\\d\\d)((\\d{1,3},)*\\d{1,3}\\.\\d\\d)((\\d{1,3},)*\\d{1,3}\\.\\d\\d)((\\d{1,3},)*\\d{1,3}\\.\\d\\d)";
        Pattern p = Pattern.compile(creditsRegex);
        Matcher matcher = p.matcher(rawString);

        if (matcher.find()) {
            double purchAndAdv = parseAmount(matcher.group(5));
            double installment = parseAmount(matcher.group(7));
            double finCharge = parseAmount(matcher.group(9));
            double lateCharge = parseAmount(matcher.group(11));                        
            totalCredits = purchAndAdv + installment + finCharge + lateCharge;
            return;
        }

        throw new IllegalStateException("Cannot find Total Credits from extracted text. Check updates in statement format");
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException when the pattern for Total Debits is not found 
     */
    @Override
    protected void extractTotalDebits() {
        String debitsRegex = "Total((\\d{1,3},)*\\d{1,3}\\.\\d\\d)((\\d{1,3},)*\\d{1,3}\\.\\d\\d)";
        Pattern p = Pattern.compile(debitsRegex);
        Matcher matcher = p.matcher(rawString);

        if (matcher.find()) {                        
            totalDebits = parseAmount(matcher.group(3));
            return;
        }

        throw new IllegalStateException("Cannot find Total Debits from extracted text. Check updates in statement format");
    }


    @Override
    protected void extractTotalAmountDue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractTotalAmountDue'");
    }


    @Override
    protected void extractTransactionList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractTransactionList'");
    }

    @Override
    public void saveToSpreadSheet() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveToSpreadSheet'");
    }


    // TODO
    public void parseOtherData() {
            // add transactions to list
            // including payment, finance charges, late payment charges
            // mainly from transaction listing (which includes installment due)

        /* installment transactions */
        // add txns to list
        // unbilled installment amount (match with remaining bal)

        /* points */
        //extractPoints();

    }
    

}
