package kpes.finapp.service;

import java.time.LocalDate;
import java.util.Objects;

public class CreditTransaction extends AbstractTransaction {

    private LocalDate postDate;

    public CreditTransaction(LocalDate transactionDate, 
                                    String description,
                                    double amount,
                                    LocalDate postDate) {

        super(transactionDate, description, amount);
        this.postDate = postDate;

    }

    public LocalDate getPostDate() {
        return postDate;
    }

    @Override
    public String toString() {
        return String.format("%tD | %tD - %s - %,.2f", transactionDate, postDate, description, amount);
    }

    //TODO fix equals
    @Override
    public boolean equals(Object o) {
        if ( o == this ) return true;
        if ( ! (o instanceof CreditTransaction) ) return false;
        return this.toString().equals(o.toString());
    }

    //TODO fix hashCode
    @Override
    public int hashCode() {
        return Objects.hash(transactionDate,description,amount,postDate);
    }
    
}
