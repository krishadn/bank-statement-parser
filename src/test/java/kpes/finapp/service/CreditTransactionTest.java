package kpes.finapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class CreditTransactionTest {

    @Test
    void testConstructorandGetters() {

        // required fields
        LocalDate date = LocalDate.now();
        String description = "Sample Description";
        float amount = 1000;
        LocalDate postDate = LocalDate.now().minusDays((long)2);

        CreditTransaction ct = new CreditTransaction(date, description, amount, postDate);

        // initialized fields
        LocalDate dateInit = ct.getTransactionDate();
        String descriptionInit = ct.getDescription();
        float amountInit = ct.getAmount();
        LocalDate postDateInit = ct.getPostDate();

        assertEquals(date, dateInit);
        assertEquals(description, descriptionInit);
        assertEquals(amount, amountInit);
        assertEquals(postDate, postDateInit);

    }


    @Test
    void testToString() {

       // required fields
        LocalDate date = LocalDate.now();
        String description = "Sample Description";
        float amount = 1000;
        LocalDate postDate = LocalDate.now().minusDays((long)2);

        CreditTransaction ct = new CreditTransaction(date, description, amount, postDate);

        String expected = String.format("%tD | %tD - %s - %,.2f", date, postDate, description, amount);

        assertEquals(expected, ct.toString());

    }


    
}
