package kpes.finapp.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import static kpes.finapp.service.utils.BankStatementFactory.createBankStatement;

import kpes.finapp.service.base.AbstractStatement;
import kpes.finapp.service.base.AbstractStatement.StatementType;
import kpes.finapp.service.utils.PDFBoxExtractor;

public class App {
    public static void main(String[] args) {

        String pdfPath = args[0];
        String excelPath = args[1];
        StatementType statementType = StatementType.valueOf(args[2]);

        Path pdfFile = Paths.get(pdfPath);
        Path excelFile = Paths.get(excelPath);

        AbstractStatement statement = createBankStatement(statementType);
        statement.extractStatementToSS(pdfFile, excelFile, new PDFBoxExtractor());

    }

}
