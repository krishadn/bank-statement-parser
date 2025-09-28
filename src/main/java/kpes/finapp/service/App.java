package kpes.finapp.service;

import static kpes.finapp.service.BankStatementParserService.getTransactions;

import java.nio.file.Path;
import java.nio.file.Paths;

import kpes.finapp.service.BankStatementParserService.Bank;

public class App {
    public static void main(String[] args) {
        
        String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\sampleStatement.pdf";
        Path pdfFile = Paths.get(pdfPath);

        String fullText = BankStatementParserService.extractText(pdfFile);
        String txtList = getTransactions(fullText, Bank.BPICC);
        System.out.println(txtList);
       

    }


}
