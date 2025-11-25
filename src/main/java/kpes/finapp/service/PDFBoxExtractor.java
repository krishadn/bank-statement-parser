package kpes.finapp.service;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFBoxExtractor implements ITextExtractor{

    // Tightly coupled with PDFBox methods since this is a specific implementation of ITextExtractor
    // ITextExtractor remains open for extention and closed for modification
    @Override
    public String extractText(Object forExtraction, Object credentials) throws IOException {
        Path path = (Path) forExtraction;
        String pwd = (String) credentials;

        String text = "";

        try {
            // TODO check if needs to close loader / file connection
            PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(path), pwd);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            text = pdfStripper.getText(document).trim();
            return text;
        } catch (IOException e) {
            System.out.println("Parsing error or decryption error. Returning empty string");
            e.printStackTrace();
            return text;
        }
    }
    
}
