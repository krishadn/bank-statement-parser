package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class BankStatementsTest {

    // Tests for extractStatementText
    
    @Test
    void testExtractStatementTextCaseNonExisting() {
        
        // Arrange
        Path p = Paths.get("nonexistent.pdf");
        ITextExtractor mockExtractor = mock(PDFBoxExtractor.class);
        Pattern pattern = Pattern.compile("");

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> BankStatements.extractStatementText(p, mockExtractor, pattern));
        assertTrue(exception.getMessage().contains("does not exist"));

    }

    @Test
    void testExtractStatementTextCaseExistingNonPDFFile() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".txt");
        ITextExtractor mockExtractor = mock(PDFBoxExtractor.class);
        Pattern pattern = Pattern.compile("");

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> BankStatements.extractStatementText(p, mockExtractor, pattern));
        assertTrue(exception.getMessage().contains("is not a PDF file"));
        
        // Clean up
        Files.delete(p);
    }


    @Test
    void testExtractStatementTextCaseNotBankStatement() throws IOException {
        
        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        Pattern pattern = Pattern.compile("Some Pattern", Pattern.CASE_INSENSITIVE);

        ITextExtractor mockExtractor = mock(PDFBoxExtractor.class);
        when(mockExtractor.extractText(p, pwd)).thenReturn("dummy text");

        // Act and Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> BankStatements.extractStatementText(p, mockExtractor, pattern));
        assertTrue(exception.getMessage().contains("not a Bank Statement"));

        // Clean up
        Files.delete(p);
    }

    @Test
    void testExtractStatementTextCaseIOException() throws IOException {

        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        Pattern pattern = Pattern.compile("");

        ITextExtractor mockExtractor = mock(PDFBoxExtractor.class);
        when(mockExtractor.extractText(p, pwd)).thenThrow(IOException.class);

        // Act
        String result = BankStatements.extractStatementText(p, mockExtractor, pattern);

        // Assert
        assertTrue(result.isEmpty());

        // Clean up
        Files.delete(p);
    }


    @Test
    void testExtractStatementTextCaseSuccess() throws IOException {
        
        // Arrange
        Path p = Files.createTempFile("statement", ".pdf");
        String pwd = "";
        Pattern pattern = Pattern.compile("bank statement", Pattern.CASE_INSENSITIVE);
        String expected = "This is a bank statement";

        ITextExtractor mockExtractor = mock(PDFBoxExtractor.class);
        when(mockExtractor.extractText(p, pwd)).thenReturn(expected);

        // Act
        String result = BankStatements.extractStatementText(p, mockExtractor, pattern);

        //Assert
        assertEquals(expected, result);
        
        // Clean up
        Files.delete(p);

    }
    
}
