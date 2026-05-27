package com.financialservices.mortgage;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class MortgageCalculatorTest {

    private final MortgageCalculator calculator = new MortgageCalculator();

    @Test
    public void testStandard30YearFixedRate() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(350000)
            .annualInterestRate(6.5)
            .termInYears(30)
            .downPayment(70000)
            .build();

        BigDecimal monthly = calculator.calculateMonthlyPayment(params);

        // $280,000 at 6.5% for 30 years => ~$1,769.79/month
        assertEquals(1769.79, monthly.doubleValue(), 0.01);
    }

    @Test
    public void testStandard15YearFixedRate() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(300000)
            .annualInterestRate(5.0)
            .termInYears(15)
            .downPayment(60000)
            .build();

        BigDecimal monthly = calculator.calculateMonthlyPayment(params);

        // $240,000 at 5.0% for 15 years => ~$1,897.90/month
        assertEquals(1897.90, monthly.doubleValue(), 0.01);
    }

    @Test
    public void testZeroInterestRate() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(120000)
            .annualInterestRate(0)
            .termInYears(10)
            .build();

        BigDecimal monthly = calculator.calculateMonthlyPayment(params);

        // $120,000 / 120 months = $1,000/month
        assertEquals(1000.00, monthly.doubleValue(), 0.01);
    }

    @Test
    public void testNoDownPayment() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(7.0)
            .termInYears(30)
            .build();

        BigDecimal monthly = calculator.calculateMonthlyPayment(params);

        // $200,000 at 7.0% for 30 years => ~$1,330.60/month
        assertEquals(1330.60, monthly.doubleValue(), 0.01);
    }

    @Test
    public void testAmortizationScheduleLength() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(250000)
            .annualInterestRate(6.0)
            .termInYears(30)
            .downPayment(50000)
            .build();

        List<AmortizationEntry> schedule = calculator.generateAmortizationSchedule(params);

        assertEquals(360, schedule.size());
        assertEquals(1, schedule.get(0).getPaymentNumber());
        assertEquals(360, schedule.get(359).getPaymentNumber());
    }

    @Test
    public void testAmortizationBalanceReachesZero() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(100000)
            .annualInterestRate(5.0)
            .termInYears(15)
            .build();

        List<AmortizationEntry> schedule = calculator.generateAmortizationSchedule(params);

        AmortizationEntry lastEntry = schedule.get(schedule.size() - 1);
        assertEquals(0, lastEntry.getRemainingBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void testInterestDecreasesOverTime() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(6.0)
            .termInYears(30)
            .build();

        List<AmortizationEntry> schedule = calculator.generateAmortizationSchedule(params);

        BigDecimal firstInterest = schedule.get(0).getInterestPortion();
        BigDecimal lastInterest = schedule.get(schedule.size() - 1).getInterestPortion();

        assertTrue("Interest should decrease over time",
            firstInterest.compareTo(lastInterest) > 0);
    }

    @Test
    public void testPrincipalIncreasesOverTime() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(200000)
            .annualInterestRate(6.0)
            .termInYears(30)
            .build();

        List<AmortizationEntry> schedule = calculator.generateAmortizationSchedule(params);

        BigDecimal firstPrincipal = schedule.get(0).getPrincipalPortion();
        BigDecimal midPrincipal = schedule.get(179).getPrincipalPortion();

        assertTrue("Principal portion should increase over time",
            midPrincipal.compareTo(firstPrincipal) > 0);
    }

    @Test
    public void testFullCalculationSummary() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(400000)
            .annualInterestRate(6.5)
            .termInYears(30)
            .downPayment(80000)
            .annualPropertyTax(4800)
            .annualInsurance(1200)
            .build();

        MortgageSummary summary = calculator.calculate(params);

        assertNotNull(summary);
        assertEquals(params, summary.getParameters());
        assertTrue(summary.getMonthlyPrincipalAndInterest().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(summary.getTotalMonthlyPayment().compareTo(
            summary.getMonthlyPrincipalAndInterest()) > 0);
        assertTrue(summary.getTotalInterest().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(summary.getTotalPayment().compareTo(params.getLoanAmount()) > 0);
        assertEquals(360, summary.getAmortizationSchedule().size());
    }

    @Test
    public void testLoanToValueRatio() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(400000)
            .annualInterestRate(6.0)
            .termInYears(30)
            .downPayment(80000)
            .build();

        MortgageSummary summary = calculator.calculate(params);

        // LTV = 320,000 / 400,000 = 80%
        assertEquals(80.00, summary.getLoanToValueRatio().doubleValue(), 0.01);
    }

    @Test
    public void testMonthlyPaymentIncludesTaxAndInsurance() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(300000)
            .annualInterestRate(5.5)
            .termInYears(30)
            .downPayment(30000)
            .annualPropertyTax(3600)
            .annualInsurance(1200)
            .build();

        MortgageSummary summary = calculator.calculate(params);

        BigDecimal monthlyPI = summary.getMonthlyPrincipalAndInterest();
        BigDecimal totalMonthly = summary.getTotalMonthlyPayment();
        BigDecimal expectedExtras = new BigDecimal("400.00"); // (3600 + 1200) / 12

        assertEquals(0, totalMonthly.compareTo(monthlyPI.add(expectedExtras)));
    }

    @Test
    public void testCumulativeInterestIncreases() {
        LoanParameters params = new LoanParameters.Builder()
            .principal(250000)
            .annualInterestRate(4.5)
            .termInYears(30)
            .build();

        List<AmortizationEntry> schedule = calculator.generateAmortizationSchedule(params);

        for (int i = 1; i < schedule.size(); i++) {
            assertTrue("Cumulative interest should only increase",
                schedule.get(i).getCumulativeInterest()
                    .compareTo(schedule.get(i - 1).getCumulativeInterest()) >= 0);
        }
    }
}
