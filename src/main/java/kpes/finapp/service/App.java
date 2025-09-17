package kpes.finapp.service;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class App {
    public static void main(String[] args) throws IOException {
        
        File pdfFile = new File("C:\\Users\\KPES\\Desktop\\services\\sampleStatement.pdf");

        PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile("C:\\Users\\KPES\\Desktop\\services\\sampleStatement.pdf"));

        PDFTextStripper pdfStripper = new PDFTextStripper();

        String text = pdfStripper.getText(document);

        System.out.println(text);

    }


}
