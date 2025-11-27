package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class InstallmentTransactionTest {

    @Test
    void testConstructorandGetters() {
        
        // required fields
        LocalDate date = LocalDate.now();
        String description = "Sample Description";
        double amount = 1000;
        LocalDate lastPaymentDate = LocalDate.now();
        double remainingBal = 1200;

        InstallmentTransaction installment = new InstallmentTransaction(date, description, amount, lastPaymentDate, remainingBal);

        // initialized fields
        LocalDate dateInit = installment.getTransactionDate();
        String descriptionInit = installment.getDescription();
        double amountInit = installment.getAmount();
        LocalDate lastPaymentDateInit = installment.getLastPaymentDate();
        double remainingBalInit = installment.getRemainingBal();

        assertEquals(date, dateInit);
        assertEquals(description, descriptionInit);
        assertEquals(amount, amountInit);
        assertEquals(lastPaymentDate, lastPaymentDateInit);
        assertEquals(remainingBal, remainingBalInit);

    }


    @Test
    void testToString() {

        // required fields
        LocalDate date = LocalDate.now();
        String description = "Sample Description";
        double amount = 1000;
        LocalDate lastPaymentDate = LocalDate.now();
        double remainingBal = 1200;

        InstallmentTransaction installment = new InstallmentTransaction(date, description, amount, lastPaymentDate, remainingBal);

        String expected = String.format("%tD - %tD - %s - %,.2f - %,.2f", date, lastPaymentDate, description, amount, remainingBal);

        assertEquals(expected, installment.toString());

    }

}
