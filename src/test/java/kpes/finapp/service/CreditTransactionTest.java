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
    void testSettersWithGetters() {

        // value before
        LocalDate date = null;
        String description = "";
        float amount = 0;

        CreditTransaction ct = new CreditTransaction(date, description, amount, date);

        // set new values with setters
        LocalDate newDate = LocalDate.now();
        String newDescription = "Sample Description";
        float newAmount = 1000;
        LocalDate newPostDate = LocalDate.now().minusDays((long)2);

        ct.setTransactionDate(newDate);
        ct.setDescription(newDescription);
        ct.setAmount(newAmount);
        ct.setPostDate(newPostDate);

        // get new values set
        LocalDate dateSet = ct.getTransactionDate();
        String descriptionSet = ct.getDescription();
        float amountSet = ct.getAmount();
        LocalDate postDateSet = ct.getPostDate();

        assertEquals(newDate, dateSet);
        assertEquals(newDescription, descriptionSet);
        assertEquals(newAmount, amountSet);
        assertEquals(newPostDate, postDateSet);

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
