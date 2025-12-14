package kpes.finapp.service;

import java.time.LocalDate;
import java.util.Objects;

public class InstallmentTransaction extends AbstractTransaction {

    private LocalDate lastPaymentDate;
    private double remainingBal;

    protected InstallmentTransaction(LocalDate transactionDate, 
                                        String description, 
                                        double amount,
                                        LocalDate lastPaymentDate,
                                        double remainingBal) {
                            
        super(transactionDate, description, amount);
        this.lastPaymentDate = lastPaymentDate;
        this.remainingBal = remainingBal;
    }

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public double getRemainingBal() {
        return remainingBal;
    }

    @Override
    public String toString() {
        return String.format("%tD - %tD - %s - %,.2f - %,.2f", transactionDate, lastPaymentDate, description, amount, remainingBal);
    }

     //TODO fix equals
    @Override
    public boolean equals(Object o) {
        if ( o == this ) return true;
        if ( ! (o instanceof InstallmentTransaction) ) return false;
        return this.toString().equals(o.toString());
    }

    //TODO fix hashCode
    @Override
    public int hashCode() {
        return Objects.hash(transactionDate,description,amount,lastPaymentDate,remainingBal);
    }

}
