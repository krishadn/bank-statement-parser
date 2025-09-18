package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static kpes.finapp.service.BankStatementParserService.extractText;

public class BankStatementParserServiceTest {

    // String paths to test files
    String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\sampleStatement.pdf";
    String testFilePath = "C:\\Users\\KPES\\Desktop\\services\\testString.txt";
    String emptyPdfPath = "C:\\Users\\KPES\\Desktop\\services\\empty.pdf";
    
  
    // extractText outputs correct String value for non empty pdf
    @Test
    void testNonEmptyPDF() {
        Path pdfFile = Paths.get(pdfPath);
        Path testFile = Paths.get(testFilePath);

        try {
            Stream<String> lines = Files.lines(testFile); 
            String data = lines.collect(Collectors.joining("\n"));
            lines.close();

            assertEquals(data, extractText(pdfFile));
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // extractText outputs correct String value for empty pdf
    @Test
    void testEmptyPDF() {
        Path emptyPdf = Paths.get(emptyPdfPath);
        assertEquals("", extractText(emptyPdf));
    }

    // extractText handles file does not exist
    @Test
    void testErrorHandling() {

    }


    
}
