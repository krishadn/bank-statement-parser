package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import kpes.finapp.service.BankStatementParserService.Bank;

import static kpes.finapp.service.BankStatementParserService.extractText;
import static kpes.finapp.service.BankStatementParserService.getTransactions;

public class BankStatementParserServiceTest {

    // String paths to test files
    String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\sampleStatement.pdf";
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
        //TODO: handling exceptions
        assertTrue(false);
    }

    // getTransactions outputs a String value for non empty pdf
    @Test
    void testNonEmptyPDFGetTransactions() {
        Path pdfFile = Paths.get(pdfPath);
        String txnList = getTransactions(extractText(pdfFile), Bank.BPICC);
        assertFalse(txnList.isEmpty() || txnList.isBlank());
    }

    // getTransactions throws an AssertionError for empty String argument
    @Test
    void testEmptyPDFGetTransactions() {
        assertThrows(AssertionError.class,() -> getTransactions(""));
    }


    
}
