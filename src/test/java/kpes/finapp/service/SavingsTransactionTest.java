package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class SavingsTransactionTest {

    @Test
    void testConstructorandGetters() {
        
        // required fields
        LocalDate date = LocalDate.now();
        String description = "Sample Description";
        double amount = 1000;
        String ref = "REF02145";

        SavingsTransaction st = new SavingsTransaction(date, description, amount, ref);

        // initialized fields
        LocalDate dateInit = st.getTransactionDate();
        String descriptionInit = st.getDescription();
        double amountInit = st.getAmount();
        String refInit = st.getReferenceNum();

        assertEquals(date, dateInit);
        assertEquals(description, descriptionInit);
        assertEquals(amount, amountInit);
        assertEquals(ref, refInit);

    }


    @Test
    void testToString() {

        // required fields
        LocalDate date = LocalDate.now();
        String description = "Sample Description";
        double amount = 1000;
        String ref = "REF02145";

        SavingsTransaction st = new SavingsTransaction(date, description, amount, ref);

        String expected = String.format("%tD - %s - %s - %,.2f", date, ref, description, amount);

        assertEquals(expected, st.toString());

    }
    

}
