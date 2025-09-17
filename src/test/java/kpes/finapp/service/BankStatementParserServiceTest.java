package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static kpes.finapp.service.BankStatementParserService.extractText;

public class BankStatementParserServiceTest {


    // extractText outputs correct String value for non empty pdf
    @Test
    void testNonEmptyPDF() {

    }

    // extractText outputs correct String value for empty pdf
    @Test
    void testEmptyPDF() {

    }

    // extractText handles file does not exist
    @Test
    void testErrorHandling() {

    }


    
}
