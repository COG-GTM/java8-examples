package com.financialservices.mortgage;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class LoanParametersTest {

    @Test
    public void testValidParameters() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(350000)
            .annualInterestRate(6.5)
            .termInYears(30)
            .downPayment(70000)
            .annualPropertyTax(4200)
            .annualInsurance(1200)
            .build();

        assertEquals(0, params.getPrincipal().compareTo(BigDecimal.valueOf(350000)));
        assertEquals(0, params.getAnnualInterestRate().compareTo(BigDecimal.valueOf(6.5)));
        assertEquals(30, params.getTermInYears());
        assertEquals(360, params.getTermInMonths());
        assertEquals(0, params.getDownPayment().compareTo(BigDecimal.valueOf(70000)));
        assertEquals(0, params.getLoanAmount().compareTo(BigDecimal.valueOf(280000)));
    }

    @Test
    public void testMonthlyPropertyTax() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(300000)
            .annualInterestRate(5.0)
            .termInYears(30)
            .annualPropertyTax(6000)
            .build();

        assertEquals(0, params.getMonthlyPropertyTax().compareTo(BigDecimal.valueOf(500).setScale(2)));
    }

    @Test
    public void testMonthlyInsurance() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(300000)
            .annualInterestRate(5.0)
            .termInYears(30)
            .annualInsurance(2400)
            .build();

        assertEquals(0, params.getMonthlyInsurance().compareTo(BigDecimal.valueOf(200).setScale(2)));
    }

    @Test
    public void testDefaultValues() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(5.0)
            .termInYears(30)
            .build();

        assertEquals(0, params.getDownPayment().compareTo(BigDecimal.ZERO));
        assertEquals(0, params.getAnnualPropertyTax().compareTo(BigDecimal.ZERO));
        assertEquals(0, params.getAnnualInsurance().compareTo(BigDecimal.ZERO));
        assertEquals(0, params.getLoanAmount().compareTo(BigDecimal.valueOf(200000)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativePrincipal() {
        new LoanParameters.Builder()
            .principal(-100000)
            .annualInterestRate(5.0)
            .termInYears(30)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroPrincipal() {
        new LoanParameters.Builder()
            .principal(0)
            .annualInterestRate(5.0)
            .termInYears(30)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeInterestRate() {
        new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(-1.0)
            .termInYears(30)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterestRateOver100() {
        new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(101)
            .termInYears(30)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroTerm() {
        new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(5.0)
            .termInYears(0)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTermExceeds50Years() {
        new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(5.0)
            .termInYears(51)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDownPaymentExceedsPrincipal() {
        new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(5.0)
            .termInYears(30)
            .downPayment(200000)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeDownPayment() {
        new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(5.0)
            .termInYears(30)
            .downPayment(-10000)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativePropertyTax() {
        new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(5.0)
            .termInYears(30)
            .annualPropertyTax(-100)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeInsurance() {
        new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(5.0)
            .termInYears(30)
            .annualInsurance(-100)
            .build();
    }

    @Test
    public void testToString() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(300000)
            .annualInterestRate(5.5)
            .termInYears(30)
            .downPayment(60000)
            .build();

        String result = params.toString();
        assertTrue(result.contains("300000"));
        assertTrue(result.contains("5.5"));
        assertTrue(result.contains("30"));
    }

    @Test
    public void testBigDecimalConstructors() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(new BigDecimal("250000"))
            .annualInterestRate(new BigDecimal("4.75"))
            .termInYears(15)
            .downPayment(new BigDecimal("50000"))
            .annualPropertyTax(new BigDecimal("3000"))
            .annualInsurance(new BigDecimal("1500"))
            .build();

        assertEquals(0, params.getPrincipal().compareTo(new BigDecimal("250000")));
        assertEquals(0, params.getAnnualInterestRate().compareTo(new BigDecimal("4.75")));
    }
}
