package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BPICreditStatementTest {

    BPICreditStatement bpicc;
    TextExtractor<Path, String> mockExtractor;

    @BeforeEach
    void setUp() {
        bpicc = new BPICreditStatement();
        mockExtractor = mock(PDFBoxExtractor.class);
    }

    /* ====================== Tests for extractStatementText ====================== */
    
    @Test
    void testExtractStatementTextCaseNonExisting() {
        
        // Arrange
        Path p = Paths.get("nonexistent.pdf");

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> bpicc.extractStatementText(p, mockExtractor));
        assertTrue(exception.getMessage().contains("does not exist"));

    }

    @Test
    void testExtractStatementTextCaseExistingNonPDFFile() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".txt");

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> bpicc.extractStatementText(p, mockExtractor));
        assertTrue(exception.getMessage().contains("is not a PDF file"));
        
        // Clean up
        Files.delete(p);
    }


    @Test
    void testExtractStatementTextCaseNotBankStatement() throws IOException {
        
        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        when(mockExtractor.extractText(p, pwd)).thenReturn("dummy text");

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> bpicc.extractStatementText(p, mockExtractor));
        assertTrue(exception.getMessage().contains("not a Bank Statement"));

        // Clean up
        Files.delete(p);
    }

    @Test
    void testExtractStatementTextCaseIOException() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        when(mockExtractor.extractText(p, pwd)).thenThrow(IOException.class);

        // Act
        Boolean result = bpicc.extractStatementText(p, mockExtractor);

        // Assert
        assertFalse(result);

        // Clean up
        Files.delete(p);
    }


    @Test
    void testExtractStatementTextCaseSuccess() throws IOException {
        
        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String expected = "<<other content>> Statement of Account <<other content>>";
        when(mockExtractor.extractText(p, pwd)).thenReturn(expected);

        // Act
        Boolean result = bpicc.extractStatementText(p, mockExtractor);

        //Assert
        assertTrue(result);
        assertEquals(expected, bpicc.getRawString());
        
        // Clean up
        Files.delete(p);

    }

    
    /* ====================== Tests for parseRawText ====================== */

    @Test
    void testParseRawTextCaseGeneral() throws IOException {

        // post conditions to test
        // IMPORTANT ensure all functions are called      

        // Arrange
        BPICreditStatement mockBpiCc = mock(BPICreditStatement.class);
        doNothing().when(mockBpiCc).preprocessRawText();
        doNothing().when(mockBpiCc).extractStatementDate();
        doNothing().when(mockBpiCc).extractDueDate();
        doNothing().when(mockBpiCc).extractMinAmtDue();
        doNothing().when(mockBpiCc).extractPreviousBalance();
        doNothing().when(mockBpiCc).extractTotalCredits();
        doNothing().when(mockBpiCc).extractTotalDebits();
        doNothing().when(mockBpiCc).extractTotalAmountDue();
        doNothing().when(mockBpiCc).extractTransactionList();
        when(mockBpiCc.isBalanced()).thenReturn(true);
        when(mockBpiCc.isTransactionComplete()).thenReturn(true);
        
        doCallRealMethod().when(mockBpiCc).parseRawText();

        // Act
        mockBpiCc.parseRawText();

        // Assert
        verify(mockBpiCc, times(1)).preprocessRawText();
        verify(mockBpiCc, times(1)).extractStatementDate();
        verify(mockBpiCc, times(1)).extractDueDate();
        verify(mockBpiCc, times(1)).extractMinAmtDue();
        verify(mockBpiCc, times(1)).extractPreviousBalance();
        verify(mockBpiCc, times(1)).extractTotalCredits();
        verify(mockBpiCc, times(1)).extractTotalDebits();
        verify(mockBpiCc, times(1)).extractTotalAmountDue();
        verify(mockBpiCc, times(1)).extractTransactionList();
        verify(mockBpiCc, times(1)).isBalanced();
        verify(mockBpiCc, times(1)).isTransactionComplete();

    }

    @Test
    void testParseRawTextCaseZeroBeginningNoTransactions() {

        // post conditions to test
        // IMPORTANT ensure all functions are called
        // 1. parsed field should be true
        // 2. begBal is zero
        // 3. total credits is zero
        // 4. total debits is zero
        // 5. ending is zero
        // 6. transaction list is empty
        // 7. minAmtDue is zero
        // 8. statement date and due date should not be equal
        // 9. rawString is preprocessed

    }

    @Test
    void testParseRawTextCaseWithBeginningAndTransactions() {

        // post conditions to test
        // IMPORTANT ensure all functions are called
        // 1. parsed field should be true
        // 2. begBal is equal to the amount in the raw text
        // 3. total credits is equal to the amount in the raw text
        // 4. total debits is equal to the amount in the raw text
        // 5. ending is equal to the amount in the raw text
        // 6. transaction list is not empty
        // 7. minAmtDue is equal to the amount in the raw text
        // 8. statement date and due date should not be equal, should be the same in the raw text
        // 9. rawString is preprocessed

    }


    @Test
    void testParseRawTextCaseStatementNotBalanced() {

        // post conditions to test

        // 1. parsed field is false
        // 2. fields are cleared with the indicated value in clearFields
        // 3. throws exception

    }

    
    /* ====================== Tests for preprocessRawText ====================== */

    @Test
    void testPreprocessRawTextCaseRemovedAllSpaces() throws IOException {
        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > > ";
        String expected = "<<othercontent>>StatementofAccount<<othercontent>>";
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.preprocessRawText();

        //Assert
        assertEquals(expected, bpicc.getRawString());
        
        // Clean up
        Files.delete(p);

    }


    /* ====================== Tests for extractStatementDate ====================== */

    @Test
    void testExtractStatementDateCasePatternFoundTwoDigitDate() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " STATEMENTDATEOCTOBER30,2025 ";
        LocalDate expected = LocalDate.of(2025, 10, 30);
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractStatementDate();

        //Assert
        assertEquals(expected, bpicc.getStatementDate());
        
        // Clean up
        Files.delete(p);

    }

    @Test
    void testExtractStatementDateCasePatternFoundOneDigitDate() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " STATEMENTDATEOCTOBER4,2025 ";
        LocalDate expected = LocalDate.of(2025, 10, 4);
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractStatementDate();

        //Assert
        assertEquals(expected, bpicc.getStatementDate());
        
        // Clean up
        Files.delete(p);

    }

    @Test
    void testExtractStatementDateCasePatternFoundOneDigitDateWithLeadingZero() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " STATEMENTDATEJANUARY04,2025 ";
        LocalDate expected = LocalDate.of(2025, 1, 4);
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractStatementDate();

        //Assert
        assertEquals(expected, bpicc.getStatementDate());
        
        // Clean up
        Files.delete(p);

    }

    @Test
    void testExtractStatementDateCasePatternNotFound() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >";
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);
        LocalDate expected = bpicc.getStatementDate();        

        // Act
        bpicc.extractStatementText(p, mockExtractor);

        //Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> bpicc.extractStatementDate());
        assertTrue(exception.getMessage().contains("Statement Date"));
        assertEquals(expected, bpicc.getStatementDate());
        
        // Clean up
        Files.delete(p);
        
    }


    /* ====================== Tests for extractDueDate ====================== */

    @Test
    void testExtractDueDateCasePatternFoundOneDigitDate() throws IOException {
        
        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " PAYMENTDUEDATENOVEMBER1,2025 ";
        LocalDate expected = LocalDate.of(2025, 11, 1);
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractDueDate();

        //Assert
        assertEquals(expected, bpicc.getDueDate());
        
        // Clean up
        Files.delete(p);

    }

    @Test
    void testExtractDueDateCasePatternFoundTwoDigitDate() throws IOException {
        
        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " PAYMENTDUEDATENOVEMBER12,2025 ";
        LocalDate expected = LocalDate.of(2025, 11, 12);
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractDueDate();

        //Assert
        assertEquals(expected, bpicc.getDueDate());
        
        // Clean up
        Files.delete(p);

    }

    @Test
    void testExtractDueDateCasePatternNotFound() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >";
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);
        LocalDate expected = bpicc.getDueDate();

        // Act
        bpicc.extractStatementText(p, mockExtractor);

        //Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> bpicc.extractDueDate());
        assertTrue(exception.getMessage().contains("Due Date"));
        assertEquals(expected, bpicc.getDueDate());
        
        // Clean up
        Files.delete(p);
        
    }


    /* ====================== Tests for extractMinAmtDue ====================== */

    @Test
    void testExtractMinAmtDueCasePatternFoundHundred() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " MINIMUMAMOUNTDUE850.00 ";
        double expected = 850;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractMinAmtDue();

        //Assert
        assertEquals(expected, bpicc.getMinAmountDue());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractMinAmtDueCasePatternFoundThousand() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " MINIMUMAMOUNTDUE1,850.00 ";
        double expected = 1850;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractMinAmtDue();

        //Assert
        assertEquals(expected, bpicc.getMinAmountDue());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractMinAmtDueCasePatternFoundMillion() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " MINIMUMAMOUNTDUE1,100,850.65 ";
        double expected = 1100850.65;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractMinAmtDue();

        //Assert
        assertEquals(expected, bpicc.getMinAmountDue());
        
        // Clean up
        Files.delete(p);


    }


    @Test
    void testExtractMinAmtDueCasePatternNotFound() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >";
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);
        double expected = 0;

        // Act
        bpicc.extractStatementText(p, mockExtractor);

        //Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> bpicc.extractMinAmtDue());
        assertTrue(exception.getMessage().contains("Minimum Amount Due"));
        assertEquals(expected, bpicc.getMinAmountDue());
        
        // Clean up
        Files.delete(p);
        
    }


    /* ====================== Tests for extractPreviousBalance ====================== */

    @Test
    void testExtractPreviousBalanceCasePatternFoundHundred() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " PreviousBalance500.25 ";
        double expected = 500.25;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractPreviousBalance();

        //Assert
        assertEquals(expected, bpicc.getBeginningBalance());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractPreviousBalanceCasePatternFoundThousand() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " PreviousBalance20,001.62 ";
        double expected = 20001.62;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractPreviousBalance();

        //Assert
        assertEquals(expected, bpicc.getBeginningBalance());
        
        // Clean up
        Files.delete(p);


    }


    @Test
    void testExtractPreviousBalanceCasePatternFoundMillion() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " PreviousBalance12,320,001.62 ";
        double expected = 12320001.62;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractPreviousBalance();

        //Assert
        assertEquals(expected, bpicc.getBeginningBalance());
        
        // Clean up
        Files.delete(p);


    }


    @Test
    void testExtractPreviousBalanceCasePatternNotFound() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >";
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);
        double expected = 0;

        // Act
        bpicc.extractStatementText(p, mockExtractor);

        //Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> bpicc.extractPreviousBalance());
        assertTrue(exception.getMessage().contains("Previous Balance"));
        assertEquals(expected, bpicc.getBeginningBalance());
        
        // Clean up
        Files.delete(p);
        
    }


    /* ====================== Tests for extractTotalCredits ====================== */

    @Test
    void testExtractTotalCreditsCasePatternFoundAllZero() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.270.000.000.000.000.00 ";
        double expected = 0;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundWithHundredPurch() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.27300.550.000.000.000.00 ";
        double expected = 300.55;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundWithThousandPurch() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.2712,300.550.000.000.000.00 ";
        double expected = 12300.55;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundWithHundredInstall() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.270.00456.780.000.000.00 ";
        double expected = 456.78;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundWithThousandInstall() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.270.0023,456.780.000.000.00 ";
        double expected = 23456.78;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundWithHundredFC() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.270.000.00567.890.000.00 ";
        double expected = 567.89;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundWithThousandFC() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.270.000.0034,567.890.000.00 ";
        double expected = 34567.89;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundWithHundredLC() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.270.000.000.00789.120.00 ";
        double expected = 789.12;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }


    @Test
    void testExtractTotalCreditsCasePatternFoundWithThousandLC() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.270.000.000.0056,789.120.00 ";
        double expected = 56789.12;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundAllHundred() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.27123.45345.6712.34789.120.00 ";
        double expected = 123.45+345.67+12.34+789.12;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalCreditsCasePatternFoundAllThousand() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2722,222.27789,123.4512,345.6789,012.3456,789.120.00 ";
        double expected = 789123.45+12345.67+89012.34+56789.12;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalCredits();

        //Assert
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);


    }


    @Test
    void testExtractTotalCreditsCasePatternNotFound() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >";
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);
        double expected = 0;

        // Act
        bpicc.extractStatementText(p, mockExtractor);

        //Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> bpicc.extractTotalCredits());
        assertTrue(exception.getMessage().contains("Total Credits"));
        assertEquals(expected, bpicc.getTotalCredits());
        
        // Clean up
        Files.delete(p);
        
    }


    /* ====================== Tests for extractTotalDebits ====================== */

    @Test
    void testExtractTotalDebitsCasePatternFoundZero() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.270.000.000.000.000.000.00 ";
        double expected = 0;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalDebits();

        //Assert
        assertEquals(expected, bpicc.getTotalDebits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalDebitsCasePatternFoundHundred() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.27989.78300.550.000.000.000.00 ";
        double expected = 989.78;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalDebits();

        //Assert
        assertEquals(expected, bpicc.getTotalDebits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalDebitsCasePatternFoundThousand() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >" +
                        " Total22,222.2720,356.9812,300.550.000.000.000.00 ";
        double expected = 20356.98;
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);

        // Act
        bpicc.extractStatementText(p, mockExtractor);
        bpicc.extractTotalDebits();

        //Assert
        assertEquals(expected, bpicc.getTotalDebits());
        
        // Clean up
        Files.delete(p);


    }

    @Test
    void testExtractTotalDebitsCasePatternNotFound() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        String content = " < < o t h e r c o n t e n t > >  Statement of Account  < < o t h e r  c o n t e n t > >";
        when(mockExtractor.extractText(p, pwd)).thenReturn(content);
        double expected = 0;

        // Act
        bpicc.extractStatementText(p, mockExtractor);

        //Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> bpicc.extractTotalDebits());
        assertTrue(exception.getMessage().contains("Total Debits"));
        assertEquals(expected, bpicc.getTotalDebits());
        
        // Clean up
        Files.delete(p);
        
    }

}
