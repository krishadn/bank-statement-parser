package kpes.finapp.service;

import static kpes.finapp.service.BankStatementParser.createDatedBankStatement;
import static kpes.finapp.service.BankStatementParser.parseData;
import static kpes.finapp.service.BankStatementParser.parseDueDate;
import static kpes.finapp.service.BankStatementParser.processData;
import static kpes.finapp.service.BankStatementParser.saveBankStatementToXlsx;
import static kpes.finapp.service.BankStatementParser.parseStatementDate;

import java.nio.file.Path;
import java.nio.file.Paths;


import kpes.finapp.service.BankStatementParser.Bank;

public class App {
    public static void main(String[] args) {

        // TODO CLI program
        
        /* 
         * Testing -------------------------------------------
         */
        String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\sampleStatement2.pdf";
        // String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\noTransactions.pdf";

        
        Path pdfFile = Paths.get(pdfPath);
        // String fullText = BankStatementParser.extractText(pdfFile);
        // String txtList = parseData(fullText, Bank.BPICC); 
        // BankStatement bs = processData(txtList, Bank.BPICC);


        BankStatement bs1 = createDatedBankStatement(pdfFile, Bank.BPICC);

        System.out.println(bs1);
        Path filePath = Paths.get("BankStatements/sample.xlsx");

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
