package kpes.finapp.service;

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
    

    @Override
    protected Pattern createPattern() {
        return Pattern.compile("Statement of Account");
    }

    @Override
    protected void preprocessRawText() {
        rawString = rawString.replaceAll(" ", "");
    }


    @Override
    protected void extractStatementDate() {
        String dateRegex = "STATEMENTDATE([A-Z]{3,9}\\d\\d?,20\\d\\d)";

        Pattern p = Pattern.compile(dateRegex);
        Matcher matcher = p.matcher(rawString);

        String rawDate = "";

        if (matcher.find()) {
            rawDate = matcher.group(1);
        }
        
        Pattern digit = Pattern.compile("\\d");
        Matcher digitMatcher = digit.matcher(rawDate);
        int digitCount = 0;
        while (digitMatcher.find()) digitCount++;


        digitMatcher.reset();
        digitMatcher.find();

        String month = rawDate.substring(1, digitMatcher.start());
        month = month.toLowerCase();
        month = rawDate.charAt(0) + month;
        
        String formattedDate = "";
        String dateYear = rawDate.substring(digitMatcher.start());

        if (digitCount < 6) {
            formattedDate = month + 0 + dateYear;
        } else {
            formattedDate = month + dateYear;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMMdd,yyyy");
        statementDate = LocalDate.parse(formattedDate, formatter);

    }


    @Override
    protected void extractDueDate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractDueDate'");
    }


    @Override
    protected void extractMinAmtDue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractMinAmtDue'");
    }


    @Override
    protected void extractPreviousBalance() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractPreviousBalance'");
    }


    @Override
    protected void extractTotalCredits() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractTotalCredits'");
    }


    @Override
    protected void extractTotalDebits() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractTotalDebits'");
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
