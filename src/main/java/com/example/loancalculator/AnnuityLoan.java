package com.example.loancalculator;

import java.util.ArrayList;

public class AnnuityLoan {
    private Double amount;
    private Double rate;
    private int years;
    private ArrayList<Termin> terminArray = new ArrayList<>();

    public AnnuityLoan(Double amount, Double rate, int years) {
        this.amount = amount * 1000000;
        this.rate = rate / 100; // gir inn renten på formen 4,5
        this.years = years;
    }

    public ArrayList<Termin> calculateTerminArray() {
        Double totalPayment;
        Double interests;
        Double principal;
        Double remainingDebt = amount;
        Double rateDecimal = rate;

        for (int year = 1; year <= years; year++) {
            totalPayment = amount * rateDecimal * Math.pow(1 + rateDecimal, (double) years) / (Math.pow(1 + rateDecimal, (double) years) - 1);   // Rente må oppgis i formen 1,045 for rente på 4,5
            interests = remainingDebt * rate;
            principal = totalPayment - interests;
            totalPayment = remainingDebt < totalPayment ? remainingDebt : totalPayment;
            remainingDebt -= principal;
            remainingDebt = remainingDebt <= 0 ? 0 : remainingDebt;

            terminArray.add(new Termin(year, totalPayment, interests, principal, remainingDebt));
        }
        return terminArray;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public ArrayList<Termin> getTerminArray() {
        return terminArray;
    }

    public void setTerminArray(ArrayList<Termin> terminArray) {
        this.terminArray = terminArray;
    }
}
