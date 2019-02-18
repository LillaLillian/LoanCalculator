package com.example.loancalculator;
// År, terminbeløp, renter, avdrag og restgjeld
public class Termin {
    private int year;
    private Double totalPayment;
    private Double interests;
    private Double principal;
    private Double remainingDebt;

    public Termin(int year, Double totalPayment, Double interests, Double principal, Double remainingDebt) {
        this.year = year;
        this.totalPayment = totalPayment;
        this.interests = interests;
        this.principal = principal;
        this.remainingDebt = remainingDebt;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Double getInterests() {
        return interests;
    }

    public void setInterests(Double interests) {
        this.interests = interests;
    }

    public Double getPrincipal() {
        return principal;
    }

    public void setPrincipal(Double principal) {
        this.principal = principal;
    }

    public Double getRemainingDebt() {
        return remainingDebt;
    }

    public void setRemainingDebt(Double remainingDebt) {
        this.remainingDebt = remainingDebt;
    }

    @Override
    public String toString() {
        return String.format("%-5d %-11.0f %-11.0f %-11.0f %.0f", year, totalPayment, interests, principal, remainingDebt);
    }
}
