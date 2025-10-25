package kpes.finapp.service;

import java.time.LocalDate;

public class CreditTransaction extends AbstractTransaction {

    private LocalDate postDate;

    public CreditTransaction(LocalDate transactionDate, 
                                    String description,
                                    float amount,
                                    LocalDate postDate) {

        super(transactionDate, description, amount);
        this.postDate = postDate;

    }

    public LocalDate getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDate postDate) {
        this.postDate = postDate;
    }

    @Override
    public String toString() {
        return String.format("%tD | %tD - %s - %,.2f", transactionDate, postDate, description, amount);
    }
    
}
