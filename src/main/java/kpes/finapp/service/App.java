package kpes.finapp.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static kpes.finapp.service.utils.BankStatementFactory.createBankStatement;

import kpes.finapp.service.base.AbstractStatement;
import kpes.finapp.service.base.AbstractStatement.StatementType;
import kpes.finapp.service.utils.PDFBoxExtractor;


public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {

        LOGGER.info("Application started");

        String pdfPath = args[0];
        String excelPath = args[1];
        StatementType statementType = StatementType.valueOf(args[2]);

        Path pdfFile = Paths.get(pdfPath);
        Path excelFile = Paths.get(excelPath);

        LOGGER.info("Parsing bank statement provided...");
        AbstractStatement statement = createBankStatement(statementType);

        LOGGER.info("Saving parsed bank statement to spreadsheet...");
        statement.extractStatementToSS(pdfFile, excelFile, new PDFBoxExtractor());


        LOGGER.info("Application finished");

    }

}
