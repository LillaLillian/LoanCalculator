package com.example.loancalculator;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class AnnuityLoanTest {

    AnnuityLoan loan = new AnnuityLoan(1.0, 4.5, 10);
    ArrayList<Termin> terminArrayList = loan.calculateTerminArray();

    @Test
    public void numberOfTermsEquals10(){
        assertThat(terminArrayList.size(), is(10));
    }

    @Test
    public void remainingDebtIsZeroAtLastTermin(){
        assertThat(terminArrayList.get(terminArrayList.size() - 1).getRemainingDebt(), is(0.0));
    }

    @Test
    public void firstTotalPaymentMatchesSecondToLastTotalPayment(){
        assertThat(terminArrayList.get(terminArrayList.size() - 2).getTotalPayment(), is(terminArrayList.get(0).getTotalPayment()));
    }


    @Test
    public void lastInterestPaymentIsSmallerThanFirst(){
        assertTrue(terminArrayList.get(0).getInterests() > terminArrayList.get(terminArrayList.size() - 1).getInterests());
    }

    @Test
    public void lastPricipalPaymentIsGreaterThanFirst(){
        assertTrue(terminArrayList.get(0).getPrincipal() < terminArrayList.get(terminArrayList.size() - 1).getPrincipal());
    }
}
