package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class PDFBoxExtractorTest {
    

    @Test
    void testExtractTextCaseEmptyPDFUnencrypted() throws IOException {

        // Arrange
        Path filePath = Paths.get("src/test/resources/empty.pdf");
        String pwd = "";
        
        //Act
        PDFBoxExtractor extractor = new PDFBoxExtractor();
        String result = extractor.extractText(filePath, pwd);

        // Assert
        assertTrue(result.isEmpty());

    }


    @Test
    void testExtractTextCaseNonEmptyPDFUnencrypted() throws IOException {

        // Arrange
        Path filePath = Paths.get("src/test/resources/content.pdf");
        String pwd = "";
        String expected = "This is a non-empty PDF file";
        
        //Act
        PDFBoxExtractor extractor = new PDFBoxExtractor();
        String result = extractor.extractText(filePath, pwd);

        // Assert
        assertEquals(expected, result);

    }


     @Test
    void testExtractTextCaseEmptyPDFEncrypted() throws IOException {

        // Arrange
        Path filePath = Paths.get("src/test/resources/empty_protected.pdf");
        String pwd = "password123";
        
        //Act
        PDFBoxExtractor extractor = new PDFBoxExtractor();
        String result = extractor.extractText(filePath, pwd);

        // Assert
        assertTrue(result.isEmpty());


    }


    @Test
    void testExtractTextCaseNonEmptyPDFEncrypted()  throws IOException{

        // Arrange
        Path filePath = Paths.get("src/test/resources/content_protected.pdf");
        String pwd = "password123";
        String expected = "This is an encrypted PDF";
        
        //Act
        PDFBoxExtractor extractor = new PDFBoxExtractor();
        String result = extractor.extractText(filePath, pwd);

        // Assert
        assertEquals(expected, result);

    }

    @Test
    void testExtractTextCaseEncryptedWrongPassword()  throws IOException{

        // Arrange
        Path filePath = Paths.get("src/test/resources/content_protected.pdf");
        String pwd = "";
        
        //Act and Assert
        PDFBoxExtractor extractor = new PDFBoxExtractor();
        assertThrows(IOException.class, () -> extractor.extractText(filePath, pwd));

    }


    @Test
    void testExtractTextCaseNotPDFFile() throws IOException {

        // Arrange
        Path filePath = Files.createTempFile("statement", ".txt");
        String pwd = "";
        
        //Act and Assert
        PDFBoxExtractor extractor = new PDFBoxExtractor();
        assertThrows(IOException.class, () -> extractor.extractText(filePath, pwd));

        // Clean up
        Files.delete(filePath);

    }



}
