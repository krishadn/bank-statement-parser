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

    /*
    * Uses "MMMMd,yyyy" date pattern
    * i.e. January1,2025 ; January12,2025
    * accepts rawDate with month in all caps
    */
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

    /**
     * {@inheritDoc}
     * @throws IllegalStateException when the pattern for Total Amount Due is not found 
     */
    @Override
    protected void extractTotalAmountDue() {
        String amountDueRegex = "TOTALAMOUNTDUE((\\d{1,3},)*\\d{1,3}\\.\\d\\d)";
        Pattern p = Pattern.compile(amountDueRegex);
        Matcher matcher = p.matcher(rawString);

        if (matcher.find()) {
            endingBalance = parseAmount(matcher.group(1));
            return;
        }

        throw new IllegalStateException("Cannot find Total Amount Due from extracted text. Check updates in statement format");
    }

    /**
     * {@inheritDoc}
     * Assumes that {@link #extractStatementDate()} has already been successfully invoked.
     * @throws IllegalStateException when the patterns for Transaction List is not found 
     */
    @Override
    protected void extractTransactionList() {

        // Purchases and Advances Transactions
        String transactionsOnly = extractTransactionsOnly().split("S.I.P.BALANCESUMMARY")[0];

        if (!transactionsOnly.isEmpty()) {
            String txnRegex = "([a-zA-Z]{3,9}\\d{1,2})([a-zA-Z]{3,9}\\d{1,2})(.+\\D{2}(:\\d{2}/\\d{2})?)(-?(\\d{1,3},)*\\d{1,3}\\.\\d{2})";
            Pattern p = Pattern.compile(txnRegex);
            Matcher matcher = p.matcher(transactionsOnly);

            while (matcher.find()){
                
                LocalDate txnDate = formatDate(matcher.group(1).trim() + "," + statementDate.getYear());
                LocalDate postDate = formatDate(matcher.group(2).trim() + "," + statementDate.getYear());
                String description = matcher.group(3);
                double amount = parseAmount(matcher.group(5));

                CreditTransaction txn = new CreditTransaction(txnDate, description, amount, postDate);
                transactions.add(txn);
            }

            if (transactions.isEmpty()) {
                throw new IllegalStateException("Did not find match using current Transaction Pattern");
            }
        }


        // Payment Transaction
        // Note: No check for invalid/outdated payment pattern
        String paymentRegex = "([a-zA-Z]{3,9}\\d{1,2})([a-zA-Z]{3,9}\\d{1,2})Payment-ThankYou(-(\\d{1,3},)*\\d{1,3}\\.\\d{2})";
        Pattern paymentPattern = Pattern.compile(paymentRegex);
        Matcher paymentMatcher = paymentPattern.matcher(rawString);
        
        if (paymentMatcher.find()) {                        
            LocalDate txnDate = formatDate(paymentMatcher.group(1).trim() + "," + statementDate.getYear());
            LocalDate postDate = formatDate(paymentMatcher.group(2).trim() + "," + statementDate.getYear());
            String description = "Payment";
            double amount = parseAmount(paymentMatcher.group(3));

            CreditTransaction txn = new CreditTransaction(txnDate, description, amount, postDate);
            transactions.add(txn);
        } 

        // Late Charges
        // Note: No check for invalid/outdated late charges pattern
        String lateRegex = "([a-zA-Z]{3,9}\\d{1,2})([a-zA-Z]{3,9}\\d{1,2})LateCharges((\\d{1,3},)*\\d{1,3}\\.\\d{2})";
        Pattern latePattern = Pattern.compile(lateRegex);
        Matcher lateMatcher = latePattern.matcher(rawString);
        
        if (lateMatcher.find()) {                        
            LocalDate txnDate = formatDate(lateMatcher.group(1).trim() + "," + statementDate.getYear());
            LocalDate postDate = formatDate(lateMatcher.group(2).trim() + "," + statementDate.getYear());
            String description = "Late Charges";
            double amount = parseAmount(lateMatcher.group(3));

            CreditTransaction txn = new CreditTransaction(txnDate, description, amount, postDate);
            transactions.add(txn);
        }


        // Finance Charges
        // Note: No check for invalid/outdated finance charges pattern
        String financeRegex = "FinanceCharge((\\d{1,3},)*\\d{1,3}\\.\\d{2})";
        Pattern financePattern = Pattern.compile(financeRegex);
        Matcher financeMatcher = financePattern.matcher(rawString);
        
        if (financeMatcher.find()) {                        
            double amount = parseAmount(financeMatcher.group(1));

            if (amount != 0) {
                CreditTransaction txn = new CreditTransaction(statementDate,"Finance Charges", amount, statementDate);
                transactions.add(txn);
            }

        }
        
        
    }

    /*
    * The format of extracted text is as follow:
    * 
    * << other contents >>
    * ######-#-##-#######-ACCOUNTHOLDERNAME
    * InstallmentPurchase: (in some cases only)
    * Month##Month##MerchantName:(##Mos.)###,###.##
    * InstallmentAmortization: (in some cases only)
    * (start of transaction listing to extract)
    * << transactions to extract >>
    * S.I.P.BALANCESUMMARY
    * << installment transaction details >>
    */
    private String extractTransactionsOnly() {
        String delimiterRegex = "\\d{6}-\\d{1}-\\d{2}-\\d{7}-\\S*\\s";
        Pattern p = Pattern.compile(delimiterRegex);
        Matcher m = p.matcher(rawString);

        if (m.find()) {
            String intermediate = p.split(rawString)[1];

            if (intermediate.contains("InstallmentAmortization:")){

                return intermediate.split("InstallmentAmortization:")[1];

            } else if (intermediate.contains("InstallmentPurchase")) {

                Pattern inmt = Pattern.compile("\\(\\d{1,3}Mos.\\)(\\d{1,3},)*\\d{1,3}\\.\\d\\d");
                Matcher inmtMatcher = inmt.matcher(intermediate);
                
                int startIndex = 0;
                while (inmtMatcher.find()) {
                    startIndex = inmtMatcher.end();
                }

                return intermediate.substring(startIndex);

            } 

            return intermediate;


        } else if (rawString.contains("UnbilledInstallmentAmount")) {

            Pattern lastLine = Pattern.compile("UnbilledInstallmentAmount(\\d{1,3},)*\\d{1,3}\\.\\d\\d");
            Matcher lastLineMatcher = lastLine.matcher(rawString);

            int lastIndex = 0;

            while(lastLineMatcher.find()) {
                lastIndex = lastLineMatcher.end();
            }

            if (lastIndex == rawString.length()) return "";

        }

        throw new IllegalStateException("Cannot split rawText using delimiter. Check format update");

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
