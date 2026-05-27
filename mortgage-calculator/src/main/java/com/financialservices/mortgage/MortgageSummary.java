package com.financialservices.mortgage;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Holds the complete results of a mortgage calculation, including monthly payment,
 * total costs, and the full amortization schedule.
 */
public final class MortgageSummary {

    private final LoanParameters parameters;
    private final BigDecimal monthlyPrincipalAndInterest;
    private final BigDecimal totalMonthlyPayment;
    private final BigDecimal totalPayment;
    private final BigDecimal totalInterest;
    private final List<AmortizationEntry> amortizationSchedule;

    public MortgageSummary(LoanParameters parameters, BigDecimal monthlyPrincipalAndInterest,
                           BigDecimal totalMonthlyPayment, BigDecimal totalPayment,
                           BigDecimal totalInterest, List<AmortizationEntry> amortizationSchedule) {
        this.parameters = parameters;
        this.monthlyPrincipalAndInterest = monthlyPrincipalAndInterest;
        this.totalMonthlyPayment = totalMonthlyPayment;
        this.totalPayment = totalPayment;
        this.totalInterest = totalInterest;
        this.amortizationSchedule = Collections.unmodifiableList(amortizationSchedule);
    }

    public LoanParameters getParameters() {
        return parameters;
    }

    public BigDecimal getMonthlyPrincipalAndInterest() {
        return monthlyPrincipalAndInterest;
    }

    public BigDecimal getTotalMonthlyPayment() {
        return totalMonthlyPayment;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public List<AmortizationEntry> getAmortizationSchedule() {
        return amortizationSchedule;
    }

    public BigDecimal getLoanToValueRatio() {
        if (parameters.getPrincipal().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return parameters.getLoanAmount()
            .divide(parameters.getPrincipal(), 4, BigDecimal.ROUND_HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }
}
