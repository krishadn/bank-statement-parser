package kpes.finapp.service;

import static kpes.finapp.service.BankStatementParser.getData;
import static kpes.finapp.service.BankStatementParser.processData;

import java.nio.file.Path;
import java.nio.file.Paths;


import kpes.finapp.service.BankStatementParser.Bank;

public class App {
    public static void main(String[] args) {

        // TODO CLI interface
        
        /* 
         * Testing -------------------------------------------
         */
        // String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\sampleStatement2.pdf";
        String pdfPath = "C:\\Users\\KPES\\Desktop\\services\\noTransactions.pdf";

        
        Path pdfFile = Paths.get(pdfPath);

        String fullText = BankStatementParser.extractText(pdfFile);
        String txtList = getData(fullText, Bank.BPICC);

        BankStatement bs = processData(txtList, Bank.BPICC);

        for (BankTransaction txn: bs.getDetails()) {
            System.out.println(txn);
        }
        System.out.println(bs.getTotalTxnAmount());

        /* 
        * Testing -------------------------------------------
        */

    }


}
