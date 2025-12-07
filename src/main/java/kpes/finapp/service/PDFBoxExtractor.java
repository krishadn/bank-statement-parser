package kpes.finapp.service;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFBoxExtractor implements TextExtractor<Path, String> {

    // Tightly coupled with PDFBox methods since this is a specific implementation of TextExtractor
    // TextExtractor remains open for extention and closed for modification
    /**
     * PDF text extractor from an external file
     * @param forExtraction Path pointing to a PDF file
     * @param credentials to use for opening encrypted PDF
     * @return text extracted from the file
     * @throws IOException if there is a problem in accessing the external file
     */
    @Override
    public String extractText(Path forExtraction, String credentials) throws IOException {

        String text = "";

        try (PDDocument document = Loader.loadPDF(forExtraction.toFile(), credentials)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            text = pdfStripper.getText(document).trim();
            return text;
        }

    }
    
}
