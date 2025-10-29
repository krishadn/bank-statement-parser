package kpes.finapp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Helper class for manipulating {@link AbstractStatement} objects
 * @author Krizzia Santillan
 */
public final class BankStatements {

    private BankStatements() {
        throw new UnsupportedOperationException("This is a helper class and cannot be instantiated.");
    };

    /**
     * Helper method to extract text from an ENCRYPTED bank statement, providing checks for validity, existence of file,
     * and file type. It also checks whether the pdf is a bank statement by checking against the {@code pattern}
     * (varies depending on specific {@link AbstractStatement} implementation)
     * @param path - path to the Bank Statement PDF file
     * @param extractor - {@link ITextExtractor} implementation
     * @param pw - password for decrypting encrypted Bank Statement PDF file
     * @param pattern - regular expression to use for checking if text extracted is from a valid bank statement 
     * @return extracted Bank Statement text
     */
    public static String extractStatementText(Path path, ITextExtractor extractor, String pw, Pattern pattern) {
        if (Files.notExists(path)) throw new IllegalArgumentException("File does not exist");
        if (!path.getFileName().toString().toLowerCase().endsWith(".pdf")) throw new IllegalArgumentException("File is not a PDF file");

        String result = "";

        try {
            result = extractor.extractText(path, pw);
            if (!pattern.matcher(result).find()) throw new IllegalArgumentException("File is not a Bank Statement");
            return result;

        } catch (IOException e) {
            System.out.println("Possible parsing error or decryption error");
            e.printStackTrace();
            return "";
        }

    }


    /**
     * Helper method to extract text from an UNENCRYPTED bank statement, providing checks for validity, existence of file,
     * and file type. It also checks whether the pdf is a bank statement by checking against the {@code pattern}
     * (varies depending on specific {@link AbstractStatement} implementation)
     * @param path - path to the UNENCRYPTED Bank Statement PDF file
     * @param extractor - {@link ITextExtractor} implementation
     * @param pattern - regular expression to use for checking if text extracted is from a valid bank statement
     * @return extracted Bank Statement text
     */
    public static String extractStatementText(Path path, ITextExtractor extractor, Pattern pattern) {
        return extractStatementText(path, extractor, "", pattern);
    }


}
