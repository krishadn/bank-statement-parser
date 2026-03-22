package kpes.finapp.service;

import static kpes.finapp.service.protoype.BankStatementParser.createDatedBankStatement;
import static kpes.finapp.service.protoype.BankStatementParser.parseData;
import static kpes.finapp.service.protoype.BankStatementParser.parseDueDate;
import static kpes.finapp.service.protoype.BankStatementParser.parseStatementDate;
import static kpes.finapp.service.protoype.BankStatementParser.processData;
import static kpes.finapp.service.protoype.BankStatementParser.saveBankStatementToXlsx;

import java.nio.file.Path;
import java.nio.file.Paths;

import kpes.finapp.service.protoype.OldBankStatement;
import kpes.finapp.service.protoype.BankStatementParser.Bank;

public class App {
    public static void main(String[] args) {

        // TODO CLI program
        
        /* 
         * Testing -------------------------------------------
         */
        // String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\sampleStatement.pdf";
        // String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\noTransactions.pdf";
        // String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\oct2025.pdf";
        // String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\nov2025.pdf";
        // String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\dec2025.pdf";
        String pdfPath = "C:\\Users\\KPES\\Desktop\\PersonalProjects\\services\\mar2026.pdf";

        Path pdfFile = Paths.get(pdfPath);


        // BPICreditStatement bpicc = new BPICreditStatement();
        // if (bpicc.extractStatementText(pdfFile, new PDFBoxExtractor())) {
        //     bpicc.preprocessRawText();
        //     System.out.println(bpicc.getRawString());
        // }

        // bpicc.extractStatementText(pdfFile, (p, c) -> "");

        // String fullText = BankStatementParser.extractText(pdfFile);
        // String txtList = parseData(fullText, Bank.BPICC); 
        // BankStatement bs = processData(txtList, Bank.BPICC);


        OldBankStatement bs1 = createDatedBankStatement(pdfFile, Bank.BPICC);

        System.out.println(bs1);
        Path filePath = Paths.get("BankStatements/mar2026.xlsx");

        saveBankStatementToXlsx(filePath, bs1);




        // for (BankTransaction txn: bs.getSummary()) {
        //     System.out.println(txn);
        // }
        // System.out.println(bs.getTotalTxnAmount());

        /* 
        * Testing -------------------------------------------
        */

    }


}
