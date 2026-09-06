package kpes.finapp.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import kpes.finapp.service.base.AbstractTransaction;
import kpes.finapp.service.base.CreditStatement;
import kpes.finapp.service.txns.CreditTransaction;
import kpes.finapp.service.txns.InstallmentTransaction;

public class BPICreditStatement extends CreditStatement {

    // summary fields
    private double unbilledInstallmentAmt;

    // installment details
    private List<AbstractTransaction> installmentTxns;

    public BPICreditStatement() {
        super();
        unbilledInstallmentAmt = 0;
        installmentTxns = new ArrayList<>();
        type = StatementType.BPICC;
    }

    /* Getters */
    public List<AbstractTransaction> getInstallmentTxns() {
        return installmentTxns;
    }

    public double getUnbilledInstallmentAmt() {
        return unbilledInstallmentAmt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Pattern createPattern() {
        return Pattern.compile("Statement of Account");
    }

    /**
     * {@inheritDoc} Uses additional methods to extract installment details and
     * unbilled installment amount.
     */
    @Override
    public void parseRawText() {
        super.parseRawText();
        extractUnbilledInstallment();
        extractInstallmentDetails();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preprocessRawText() {
        rawString = rawString.replaceAll(" ", "");
    }

    /**
     * {@inheritDoc} Uses additional function {@link #formatDate(String)} to fix
     * extracted date format before converting it to {@link LocalDate} type.
     * 
     * @throws IllegalStateException when the pattern for Statement Date is not
     *                               found
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

        throw new IllegalStateException(
                "Cannot find Statement Date from extracted text. Check updates in statement format");

    }

    /**
     * {@inheritDoc} Uses additional function {@link #formatDate(String)} to fix
     * extracted date format before converting it to {@link LocalDate} type.
     * 
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
     * Uses "MMMMd,yyyy" date pattern i.e. January1,2025 ; January12,2025 accepts
     * rawDate with month in all caps
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
     * 
     * @throws IllegalStateException when the pattern for Minimum Amount Due is not
     *                               found
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

        throw new IllegalStateException(
                "Cannot find Minimum Amount Due from extracted text. Check updates in statement format");

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
     * 
     * @throws IllegalStateException when the pattern for Previous Balance is not
     *                               found
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

        throw new IllegalStateException(
                "Cannot find Previous Balance from extracted text. Check updates in statement format");
    }

    /**
     * {@inheritDoc}
     * 
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

        throw new IllegalStateException(
                "Cannot find Total Credits from extracted text. Check updates in statement format");
    }

    /**
     * {@inheritDoc}
     * 
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

        throw new IllegalStateException(
                "Cannot find Total Debits from extracted text. Check updates in statement format");
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IllegalStateException when the pattern for Total Amount Due is not
     *                               found
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

        throw new IllegalStateException(
                "Cannot find Total Amount Due from extracted text. Check updates in statement format");
    }

    /**
     * {@inheritDoc} Assumes that {@link #extractStatementDate()} has already been
     * successfully invoked.
     * 
     * @throws IllegalStateException when the patterns for Transaction List is not
     *                               found
     */
    @Override
    protected void extractTransactionList() {

        assert statementDate != null : "Statement Date must be successfully extracted before extracting transactions";

        // Purchases and Advances Transactions
        String transactionsOnly = extractTransactionsOnly().split("InstallmentBalanceSummary")[0];

        if (!transactionsOnly.isEmpty()) {
            String txnRegex = "([a-zA-Z]{3,9}\\d{1,2})([a-zA-Z]{3,9}\\d{1,2})(.+?((:\\d{2}/\\d{2})|(\\s[a-zA-Z]+\\d{1,3}\\.\\d{2})|(\\d{11}))?)(-?(\\d{1,3},)*\\d{1,3}\\.\\d{2})";
            Pattern p = Pattern.compile(txnRegex,Pattern.DOTALL);
            Matcher matcher = p.matcher(transactionsOnly);

            while (matcher.find()) {

                LocalDate txnDate = formatDate(matcher.group(1).trim() + "," + statementDate.getYear());
                LocalDate postDate = formatDate(matcher.group(2).trim() + "," + statementDate.getYear());
                String description = matcher.group(3);
                double amount = parseAmount(matcher.group(8));

                AbstractTransaction txn = new CreditTransaction(txnDate, description, amount, postDate);
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

            AbstractTransaction txn = new CreditTransaction(txnDate, description, amount, postDate);
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

            AbstractTransaction txn = new CreditTransaction(txnDate, description, amount, postDate);
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
                AbstractTransaction txn = new CreditTransaction(statementDate, "Finance Charges", amount,
                        statementDate);
                transactions.add(txn);
            }

        }

    }

    /*
     * The format of extracted text is as follow:
     * 
     * << other contents >> ######-#-##-#######-ACCOUNTHOLDERNAME
     * InstallmentPurchase: (in some cases only)
     * Month##Month##MerchantName:(##Mos.)###,###.## InstallmentAmortization: (in
     * some cases only) (start of transaction listing to extract) << transactions to
     * extract >> InstallmentBalanceSummary << installment transaction details >>
     */
    private String extractTransactionsOnly() {
        String delimiterRegex = "\\d{6}-\\d{1}-\\d{2}-\\d{7}-\\S*\\s";
        Pattern p = Pattern.compile(delimiterRegex);
        Matcher m = p.matcher(rawString);

        if (m.find()) {
            String intermediate = p.split(rawString)[1];

            if (intermediate.contains("InstallmentAmortization:")) {

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

            while (lastLineMatcher.find()) {
                lastIndex = lastLineMatcher.end();
            }

            if (lastIndex == rawString.length())
                return "";

        }

        throw new IllegalStateException("Cannot split rawText using delimiter. Check format update");

    }

    /**
     * Extracts the Installment Details from the preprocessed {@link #rawString} and
     * adds extracted transactions to the {@link #installmentTxns} field.
     * 
     * @throws IllegalStateException when the patterns for Installment Details is
     *                               not found
     */
    public void extractInstallmentDetails() {

        if (!rawString.contains("InstallmentBalanceSummary"))
            return;

        String transactionsOnly = extractTransactionsOnly().split("InstallmentBalanceSummary")[1];

        if (!transactionsOnly.isEmpty()) {
            String installmentRegex = "(\\d{6})(\\d{6})(.+\\D{2})((\\d{1,3},)*\\d{1,3}\\.\\d{2})((\\d{1,3},)*\\d{1,3}\\.\\d{2})";
            Pattern p = Pattern.compile(installmentRegex);
            Matcher matcher = p.matcher(transactionsOnly);

            while (matcher.find()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyy");
                LocalDate txnDate = LocalDate.parse(matcher.group(1), formatter);
                LocalDate lastPaymentDate = LocalDate.parse(matcher.group(2), formatter);
                String description = matcher.group(3);
                double amount = parseAmount(matcher.group(4));
                double bal = parseAmount(matcher.group(6));
                AbstractTransaction txn = new InstallmentTransaction(txnDate, description, amount, lastPaymentDate,
                        bal);
                installmentTxns.add(txn);
            }

            if (installmentTxns.isEmpty()) {
                throw new IllegalStateException("Did not find match using current Installment Details Pattern");
            }
        }

    }

    /**
     * Extracts the Unbilled Installment Amount from the preprocessed
     * {@link #rawString} and assigns the extracted value to the
     * {@link #unbilledInstallmentAmt} field.
     */
    public void extractUnbilledInstallment() {

        String unbilledRegex = "UnbilledInstallmentAmount((\\d{1,3},)*\\d{1,3}\\.\\d\\d)";
        Pattern p = Pattern.compile(unbilledRegex);
        Matcher matcher = p.matcher(rawString);

        if (matcher.find()) {
            unbilledInstallmentAmt = parseAmount(matcher.group(1));
            return;
        }

        throw new IllegalStateException(
                "Cannot find Unbilled Installment Amount from extracted text. Check updates in statement format");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToSpreadSheet(Path p) {

        Workbook wb = new XSSFWorkbook();
        Sheet summarySheet = wb.createSheet("Summary");
        Sheet detailsSheet = wb.createSheet("Transactions");
        Sheet installmentSheet = wb.createSheet("Installment");

        enterSummaryData(summarySheet, wb);
        enterDetails(detailsSheet, wb);
        enterInstallmentData(installmentSheet, wb);

        try (OutputStream fileOut = Files.newOutputStream(p, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            wb.write(fileOut);
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void enterSummaryData(Sheet sheet, Workbook wb) {
        int currentRow = 0;

        sheet.setDisplayGridlines(false);

        /* Header */
        CellStyle headerStyle = defineStyle(wb, DataType.STRING, true);
        Cell headerCell = sheet.createRow(currentRow++).createCell(0);
        headerCell.setCellValue("Summary");
        headerCell.setCellStyle(headerStyle);

        /* Contents */
        CellStyle contentLabelStyle = defineStyle(wb, DataType.STRING, false);
        CellStyle contentDateStyle = defineStyle(wb, DataType.DATE, false);
        CellStyle contentAmountStyle = defineStyle(wb, DataType.AMOUNT, false);

        // Statement Type
        addSummaryContentRow(sheet.createRow(currentRow++), "Bank Statement Type", type.getFullType(),
                contentLabelStyle, contentLabelStyle, DataType.STRING);

        // Statement Date
        addSummaryContentRow(sheet.createRow(currentRow++), "Statement Date", statementDate, contentLabelStyle,
                contentDateStyle, DataType.DATE);

        // Due Date
        addSummaryContentRow(sheet.createRow(currentRow++), "Due Date", dueDate, contentLabelStyle, contentDateStyle,
                DataType.DATE);

        // Minimum Amount Due
        addSummaryContentRow(sheet.createRow(currentRow++), "Minimum Amount Due", minAmountDue, contentLabelStyle,
                contentAmountStyle, DataType.AMOUNT);

        // Unbilled Installment Amount
        addSummaryContentRow(sheet.createRow(currentRow++), "Unbilled Installment Amount", unbilledInstallmentAmt,
                contentLabelStyle, contentAmountStyle, DataType.AMOUNT);

        currentRow++; // empty row

        // Previous Balance
        addSummaryContentRow(sheet.createRow(currentRow++), "Previous Balance", beginningBalance, contentLabelStyle,
                contentAmountStyle, DataType.AMOUNT);

        // Total Credits
        addSummaryContentRow(sheet.createRow(currentRow++), "Total Credits", totalCredits, contentLabelStyle,
                contentAmountStyle, DataType.AMOUNT);

        // Total Debits
        addSummaryContentRow(sheet.createRow(currentRow++), "Total Debits", (totalDebits * -1), contentLabelStyle,
                contentAmountStyle, DataType.AMOUNT);

        // Total Amount Due
        addSummaryContentRow(sheet.createRow(currentRow++), "Total Amount Due", endingBalance, contentLabelStyle,
                contentAmountStyle, DataType.AMOUNT);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

    }

    private void enterDetails(Sheet sheet, Workbook wb) {
        int currentRow = 0;

        sheet.setDisplayGridlines(false);

        /* Header */
        CellStyle headerStyle = defineStyle(wb, DataType.STRING, true);
        Row headerRow = sheet.createRow(currentRow++);
        String[] headers = { "Transaction Date", "Post Date", "Description", "Amount" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i, CellType.STRING);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        /* Contents */
        CellStyle contentLabelStyle = defineStyle(wb, DataType.STRING, false);
        CellStyle contentDateStyle = defineStyle(wb, DataType.DATE, false);
        CellStyle contentAmountStyle = defineStyle(wb, DataType.AMOUNT, false);

        for (AbstractTransaction txn : transactions) {

            Row row = sheet.createRow(currentRow++);
            CreditTransaction creditTxn = (CreditTransaction) txn;

            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(creditTxn.getTransactionDate());
            dateCell.setCellStyle(contentDateStyle);

            Cell postDateCell = row.createCell(1);
            postDateCell.setCellValue(creditTxn.getPostDate());
            postDateCell.setCellStyle(contentDateStyle);

            Cell descCell = row.createCell(2);
            descCell.setCellValue(creditTxn.getDescription());
            descCell.setCellStyle(contentLabelStyle);

            Cell amountCell = row.createCell(3);
            amountCell.setCellValue(creditTxn.getAmount());
            amountCell.setCellStyle(contentAmountStyle);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

    }

    private void enterInstallmentData(Sheet sheet, Workbook wb) {
        int currentRow = 0;

        sheet.setDisplayGridlines(false);

        /* Header */
        CellStyle headerStyle = defineStyle(wb, DataType.STRING, true);
        Row headerRow = sheet.createRow(currentRow++);
        String[] headers = { "Transaction Date", "Last Payment Date", "Description", "Amount", "Remaining Balance" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i, CellType.STRING);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        /* Contents */
        CellStyle contentLabelStyle = defineStyle(wb, DataType.STRING, false);
        CellStyle contentDateStyle = defineStyle(wb, DataType.DATE, false);
        CellStyle contentAmountStyle = defineStyle(wb, DataType.AMOUNT, false);

        for (AbstractTransaction txn : installmentTxns) {

            Row row = sheet.createRow(currentRow++);
            InstallmentTransaction inmt = (InstallmentTransaction) txn;

            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(inmt.getTransactionDate());
            dateCell.setCellStyle(contentDateStyle);

            Cell lastPaymentDateCell = row.createCell(1);
            lastPaymentDateCell.setCellValue(inmt.getLastPaymentDate());
            lastPaymentDateCell.setCellStyle(contentDateStyle);

            Cell descCell = row.createCell(2);
            descCell.setCellValue(inmt.getDescription());
            descCell.setCellStyle(contentLabelStyle);

            Cell amountCell = row.createCell(3);
            amountCell.setCellValue(inmt.getAmount());
            amountCell.setCellStyle(contentAmountStyle);

            Cell balCell = row.createCell(4);
            balCell.setCellValue(inmt.getRemainingBal());
            balCell.setCellStyle(contentAmountStyle);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);

    }

    private CellStyle defineStyle(Workbook wb, DataType dataType, boolean isHeader) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        DataFormat df = wb.createDataFormat();

        font.setBold(isHeader);
        font.setFontName("Arial Narrow");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        switch (dataType) {
        case DATE:
            style.setDataFormat(df.getFormat("m/d/yy"));
            break;
        case AMOUNT:
            style.setDataFormat(df.getFormat("#,##0.00"));
            break;
        case STRING:
            break;
        }

        return style;
    }

    private void addSummaryContentRow(Row row, String label, Object value, CellStyle labelStyle, CellStyle valueStyle,
            DataType valueType) {

        Cell labelCell = row.createCell(0, CellType.STRING);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(1);

        switch (valueType) {
        case STRING:
            String stringValue = String.valueOf(value);
            valueCell.setCellValue(stringValue);
            break;
        case DATE:
            LocalDate dateValue = (LocalDate) value;
            valueCell.setCellValue(dateValue);
            break;
        case AMOUNT:
            Double amountValue = (Double) value;
            valueCell.setCellValue(amountValue);
            break;
        }

        valueCell.setCellStyle(valueStyle);

    }

}
