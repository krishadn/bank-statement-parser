package kpes.finapp.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import static kpes.finapp.service.protoype.BankStatementParser.createDatedBankStatement;
import static kpes.finapp.service.protoype.BankStatementParser.parseData;
import static kpes.finapp.service.protoype.BankStatementParser.parseDueDate;
import static kpes.finapp.service.protoype.BankStatementParser.parseStatementDate;
import static kpes.finapp.service.protoype.BankStatementParser.processData;
import static kpes.finapp.service.protoype.BankStatementParser.saveBankStatementToXlsx;

import kpes.finapp.service.protoype.OldBankStatement;
import kpes.finapp.service.txns.CreditTransaction;
import kpes.finapp.service.txns.InstallmentTransaction;
import kpes.finapp.service.base.AbstractTransaction;
import kpes.finapp.service.protoype.BankStatementParser.Bank;

import kpes.finapp.service.utils.PDFBoxExtractor;

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
        // String pdfPath =
        // "C:\\Users\\KPES\\Desktop\\PersonalProjects\\services\\mar2026.pdf";
        String pdfPath = "/home/kpes/Desktop/PersonalProjects/JavaProgramming/BE20260115.pdf";

        Path pdfFile = Paths.get(pdfPath);

        Path outputPath = Paths.get("/home/kpes/Desktop/PersonalProjects/JavaProgramming/test.xlsx");

        BPICreditStatement bpicc = new BPICreditStatement();
        bpicc.extractStatementToSS(pdfFile, outputPath, new PDFBoxExtractor());

        // if (bpicc.extractStatementText(pdfFile, new PDFBoxExtractor())) {

        // bpicc.parseRawText();
        // bpicc.saveToSpreadSheet(outputPath);

        // }

        // --------------- temp file generation -------------------

        // OldBankStatement bs1 = createDatedBankStatement(pdfFile, Bank.BPICC);

        // System.out.println(bs1);

        // saveBankStatementToXlsx(outputPath, bs1);

        // --------------- temp file generation -------------------

        /*
         * Testing -------------------------------------------
         */

    }

}
