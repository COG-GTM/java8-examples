package com.financialservices.mortgage;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Formats mortgage calculation results for console output.
 */
public final class MortgageReportFormatter {

    private static final String DIVIDER = String.join("", Collections.nCopies(90, "="));
    private static final String THIN_DIVIDER = String.join("", Collections.nCopies(90, "-"));

    public String formatSummary(MortgageSummary summary) {
        StringBuilder sb = new StringBuilder();
        LoanParameters params = summary.getParameters();

        sb.append("\n").append(DIVIDER).append("\n");
        sb.append("                      MORTGAGE LOAN SUMMARY\n");
        sb.append(DIVIDER).append("\n\n");

        sb.append("  LOAN DETAILS\n");
        sb.append(THIN_DIVIDER).append("\n");
        sb.append(String.format("  Home Price:                    $%,15.2f%n", params.getPrincipal()));
        sb.append(String.format("  Down Payment:                  $%,15.2f%n", params.getDownPayment()));
        sb.append(String.format("  Loan Amount:                   $%,15.2f%n", params.getLoanAmount()));
        sb.append(String.format("  Annual Interest Rate:          %15.3f%%%n", params.getAnnualInterestRate()));
        sb.append(String.format("  Loan Term:                     %12d years%n", params.getTermInYears()));
        sb.append(String.format("  Loan-to-Value Ratio:           %14.2f%%%n", summary.getLoanToValueRatio()));
        sb.append("\n");

        sb.append("  MONTHLY PAYMENT BREAKDOWN\n");
        sb.append(THIN_DIVIDER).append("\n");
        sb.append(String.format("  Principal & Interest:          $%,15.2f%n", summary.getMonthlyPrincipalAndInterest()));

        if (params.getAnnualPropertyTax().compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("  Property Tax:                  $%,15.2f%n", params.getMonthlyPropertyTax()));
        }
        if (params.getAnnualInsurance().compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("  Homeowner's Insurance:         $%,15.2f%n", params.getMonthlyInsurance()));
        }

        sb.append(String.format("  Total Monthly Payment:         $%,15.2f%n", summary.getTotalMonthlyPayment()));
        sb.append("\n");

        sb.append("  TOTAL COST OVER LOAN TERM\n");
        sb.append(THIN_DIVIDER).append("\n");
        sb.append(String.format("  Total Interest Paid:           $%,15.2f%n", summary.getTotalInterest()));
        sb.append(String.format("  Total Amount Paid:             $%,15.2f%n", summary.getTotalPayment()));
        sb.append("\n");

        return sb.toString();
    }

    public String formatAmortizationSchedule(MortgageSummary summary) {
        List<AmortizationEntry> schedule = summary.getAmortizationSchedule();
        StringBuilder sb = new StringBuilder();

        sb.append(DIVIDER).append("\n");
        sb.append("                    AMORTIZATION SCHEDULE\n");
        sb.append(DIVIDER).append("\n");
        sb.append(String.format(" %4s  |  %14s  |  %14s  |  %14s  |  %14s  |  %14s%n",
            "#", "Payment", "Principal", "Interest", "Balance", "Cum. Interest"));
        sb.append(THIN_DIVIDER).append("\n");

        for (AmortizationEntry entry : schedule) {
            sb.append(entry.toString()).append("\n");
        }

        sb.append(DIVIDER).append("\n");
        return sb.toString();
    }

    public String formatYearlySummary(MortgageSummary summary) {
        List<AmortizationEntry> schedule = summary.getAmortizationSchedule();
        StringBuilder sb = new StringBuilder();

        sb.append(DIVIDER).append("\n");
        sb.append("                    YEARLY SUMMARY\n");
        sb.append(DIVIDER).append("\n");
        sb.append(String.format(" %4s  |  %14s  |  %14s  |  %14s  |  %14s%n",
            "Year", "Principal Paid", "Interest Paid", "End Balance", "Cum. Interest"));
        sb.append(THIN_DIVIDER).append("\n");

        BigDecimal yearlyPrincipal = BigDecimal.ZERO;
        BigDecimal yearlyInterest = BigDecimal.ZERO;
        int year = 1;

        for (AmortizationEntry entry : schedule) {
            yearlyPrincipal = yearlyPrincipal.add(entry.getPrincipalPortion());
            yearlyInterest = yearlyInterest.add(entry.getInterestPortion());

            if (entry.getPaymentNumber() % 12 == 0 || entry.getPaymentNumber() == schedule.size()) {
                sb.append(String.format(" %4d  |  $%,12.2f  |  $%,12.2f  |  $%,12.2f  |  $%,12.2f%n",
                    year, yearlyPrincipal, yearlyInterest,
                    entry.getRemainingBalance(), entry.getCumulativeInterest()));
                yearlyPrincipal = BigDecimal.ZERO;
                yearlyInterest = BigDecimal.ZERO;
                year++;
            }
        }

        sb.append(DIVIDER).append("\n");
        return sb.toString();
    }
}
