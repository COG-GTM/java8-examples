package com.financialservices.mortgage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Core engine for mortgage loan calculations. Supports fixed-rate mortgages
 * with optional property tax, insurance, and down payment.
 */
public final class MortgageCalculator {

    private static final int SCALE = 10;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final MathContext MC = new MathContext(20, ROUNDING);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /**
     * Calculates the fixed monthly principal-and-interest payment.
     *
     * Formula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
     * where P = loan amount, r = monthly rate, n = number of payments
     */
    public BigDecimal calculateMonthlyPayment(LoanParameters params) {
        BigDecimal loanAmount = params.getLoanAmount();
        BigDecimal monthlyRate = getMonthlyRate(params.getAnnualInterestRate());
        int numberOfPayments = params.getTermInMonths();

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return loanAmount.divide(BigDecimal.valueOf(numberOfPayments), 2, ROUNDING);
        }

        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRtoN = pow(onePlusR, numberOfPayments);

        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(onePlusRtoN);
        BigDecimal denominator = onePlusRtoN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, ROUNDING);
    }

    /**
     * Generates the full amortization schedule for the loan.
     */
    public List<AmortizationEntry> generateAmortizationSchedule(LoanParameters params) {
        BigDecimal monthlyPayment = calculateMonthlyPayment(params);
        BigDecimal monthlyRate = getMonthlyRate(params.getAnnualInterestRate());
        BigDecimal balance = params.getLoanAmount();
        BigDecimal cumulativeInterest = BigDecimal.ZERO;
        int numberOfPayments = params.getTermInMonths();

        List<AmortizationEntry> schedule = new ArrayList<>(numberOfPayments);

        for (int i = 1; i <= numberOfPayments; i++) {
            BigDecimal interestPortion = balance.multiply(monthlyRate)
                .setScale(2, ROUNDING);

            BigDecimal principalPortion;
            BigDecimal payment;

            if (i == numberOfPayments) {
                principalPortion = balance;
                payment = balance.add(interestPortion);
            } else {
                principalPortion = monthlyPayment.subtract(interestPortion);
                payment = monthlyPayment;
            }

            balance = balance.subtract(principalPortion);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                balance = BigDecimal.ZERO;
            }
            cumulativeInterest = cumulativeInterest.add(interestPortion);

            schedule.add(new AmortizationEntry(
                i, payment, principalPortion, interestPortion,
                balance.setScale(2, ROUNDING), cumulativeInterest.setScale(2, ROUNDING)));
        }

        return schedule;
    }

    /**
     * Produces a complete mortgage summary with all calculated values.
     */
    public MortgageSummary calculate(LoanParameters params) {
        BigDecimal monthlyPI = calculateMonthlyPayment(params);
        List<AmortizationEntry> schedule = generateAmortizationSchedule(params);

        BigDecimal monthlyExtras = params.getMonthlyPropertyTax()
            .add(params.getMonthlyInsurance());
        BigDecimal totalMonthly = monthlyPI.add(monthlyExtras);

        BigDecimal totalInterest = BigDecimal.ZERO;
        for (AmortizationEntry entry : schedule) {
            totalInterest = totalInterest.add(entry.getInterestPortion());
        }

        BigDecimal totalPayment = params.getLoanAmount().add(totalInterest)
            .add(monthlyExtras.multiply(BigDecimal.valueOf(params.getTermInMonths())));

        return new MortgageSummary(
            params, monthlyPI, totalMonthly,
            totalPayment.setScale(2, ROUNDING),
            totalInterest.setScale(2, ROUNDING),
            schedule);
    }

    private BigDecimal getMonthlyRate(BigDecimal annualRate) {
        return annualRate.divide(HUNDRED, SCALE, ROUNDING)
            .divide(TWELVE, SCALE, ROUNDING);
    }

    private BigDecimal pow(BigDecimal base, int exponent) {
        BigDecimal result = BigDecimal.ONE;
        for (int i = 0; i < exponent; i++) {
            result = result.multiply(base, MC);
        }
        return result;
    }
}
