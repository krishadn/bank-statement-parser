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
        float amount = 1000;
        String ref = "REF02145";

        SavingsTransaction st = new SavingsTransaction(date, description, amount, ref);

        // initialized fields
        LocalDate dateInit = st.getTransactionDate();
        String descriptionInit = st.getDescription();
        float amountInit = st.getAmount();
        String refInit = st.getReferenceNum();

        assertEquals(date, dateInit);
        assertEquals(description, descriptionInit);
        assertEquals(amount, amountInit);
        assertEquals(ref, refInit);

    }


    @Test
    void testSettersWithGetters() {

        // value before
        LocalDate date = null;
        String description = "";
        float amount = 0;
        String ref = "";

        SavingsTransaction st = new SavingsTransaction(date, description, amount, ref);

        // set new values with setters
        LocalDate newDate = LocalDate.now();
        String newDescription = "Sample Description";
        float newAmount = 1000;
        String newRef = "REF123";

        st.setTransactionDate(newDate);
        st.setDescription(newDescription);
        st.setAmount(newAmount);
        st.setReferenceNum(newRef);

        // get new values set
        LocalDate dateSet = st.getTransactionDate();
        String descriptionSet = st.getDescription();
        float amountSet = st.getAmount();
        String refSet = st.getReferenceNum();

        assertEquals(newDate, dateSet);
        assertEquals(newDescription, descriptionSet);
        assertEquals(newAmount, amountSet);
        assertEquals(newRef, refSet);

    }


    @Test
    void testToString() {

        // required fields
        LocalDate date = LocalDate.now();
        String description = "Sample Description";
        float amount = 1000;
        String ref = "REF02145";

        SavingsTransaction st = new SavingsTransaction(date, description, amount, ref);

        String expected = String.format("%tD - %s - %s - %,.2f", date, ref, description, amount);

        assertEquals(expected, st.toString());

    }
    

}
