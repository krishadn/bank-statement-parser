package kpes.finapp.service.utils;

import kpes.finapp.service.BPICreditStatement;
import kpes.finapp.service.base.AbstractStatement;
import kpes.finapp.service.base.AbstractStatement.StatementType;

public class BankStatementFactory {

    public static AbstractStatement createBankStatement(StatementType statementType) {

        switch (statementType) {
        case BPICC:
            return new BPICreditStatement();
        default:
            throw new IllegalArgumentException("Invalid statement type: " + statementType);
        }
    }

}
