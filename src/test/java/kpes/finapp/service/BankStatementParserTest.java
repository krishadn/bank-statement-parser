package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import kpes.finapp.service.BankStatementParser.Bank;

import static kpes.finapp.service.BankStatementParser.extractText;
import static kpes.finapp.service.BankStatementParser.parseData;
import static kpes.finapp.service.BankStatementParser.processData;

public class BankStatementParserTest {

    // String paths to test files
    String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\sampleStatement2.pdf";
    String noTransactionStatement = "C:\\Users\\KPES\\Desktop\\services\\noTransactions.pdf";
    String emptyPdfPath = "C:\\Users\\KPES\\Desktop\\services\\empty.pdf";
    
  
    // extractText outputs a String value for non empty pdf
    @Test
    void testNonEmptyPDFExtractText() {
        Path pdfFile = Paths.get(pdfPath);
        String fullText = extractText(pdfFile); 
        assertFalse(fullText.isEmpty() || fullText.isBlank());
    }

    // extractText outputs empty String value for empty pdf
    @Test
    void testEmptyPDFExtractText() {
        Path emptyPdf = Paths.get(emptyPdfPath);
        assertEquals("", extractText(emptyPdf));
    }

    // extractText handles file does not exist
    @Test
    void testErrorHandlingExtractText() {
        assertEquals("Invalid path", extractText(Paths.get("non-existing_file.pdf")));

    }

    // getData outputs a String value for non empty pdf
    @Test
    void testNonEmptyPDFGetData() {
        Path pdfFile = Paths.get(pdfPath);
        String txnList = parseData(extractText(pdfFile), Bank.BPICC);
        assertFalse(txnList.isEmpty() || txnList.isBlank());
    }

    // getData throws an AssertionError for empty String argument
    @Test
    void testEmptyPDFGetData() {
        assertThrows(AssertionError.class,() -> parseData(""));
    }

    // getData throws an AssertionError for invalid PDF content
    @Test
    void testInvalidFormatGetData() {
        assertThrows(AssertionError.class,() -> parseData("Something"));
    }


    // processData returns a valid BankStatement object for valid BPICC Bank Statement PDF
    @Test
    void testValidBankStatementBPICCProcessData() {
        Path pdfFile = Paths.get(pdfPath);
        String txnList = parseData(extractText(pdfFile), Bank.BPICC);
        assertInstanceOf(OldBankStatement.class, processData(txnList, Bank.BPICC));
    }

    // processData returns a BankStatement with populated list of summarized data
    @Test
    void testNonEmptySummaryBPICCProcessData() {
        Path pdfFile = Paths.get(pdfPath);
        String txnList = parseData(extractText(pdfFile), Bank.BPICC);
        OldBankStatement bs = processData(txnList, Bank.BPICC);
        assertTrue(!bs.getSummary().isEmpty());
    }

    // processData returns a BankStatement populated list of summarized data even for statement with no transactions
    @Test
    void testSummaryForNoTxnBPICCProcessData() {
        Path pdfFile = Paths.get(noTransactionStatement);
        String txnList = parseData(extractText(pdfFile), Bank.BPICC);
        OldBankStatement bs = processData(txnList, Bank.BPICC);
        assertTrue(!bs.getSummary().isEmpty());
    }

    // processData returns a BankStatement with populated list of transaction details for statement with transactions
    @Test
    void testNonEmptyDetailsBPICCProcessData() {
        Path pdfFile = Paths.get(pdfPath);
        String txnList = parseData(extractText(pdfFile), Bank.BPICC);
        OldBankStatement bs = processData(txnList, Bank.BPICC);
        assertTrue(!bs.getDetails().isEmpty());
    }

    // processData returns a BankStatement with empty list of transaction details for statement with no transactions
    @Test
    void testEmptyDetailsBPICCProcessData() {
        Path pdfFile = Paths.get(noTransactionStatement);
        String txnList = parseData(extractText(pdfFile), Bank.BPICC);
        OldBankStatement bs = processData(txnList, Bank.BPICC);
        assertTrue(bs.getDetails().isEmpty());
    }


    // negative testing for processData 
    // - if format changed
    // TODO

    
}
