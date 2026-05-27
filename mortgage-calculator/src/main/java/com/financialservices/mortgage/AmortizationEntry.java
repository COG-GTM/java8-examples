package com.financialservices.mortgage;

import java.math.BigDecimal;

/**
 * Represents a single row in an amortization schedule.
 */
public final class AmortizationEntry {

    private final int paymentNumber;
    private final BigDecimal payment;
    private final BigDecimal principalPortion;
    private final BigDecimal interestPortion;
    private final BigDecimal remainingBalance;
    private final BigDecimal cumulativeInterest;

    public AmortizationEntry(int paymentNumber, BigDecimal payment, BigDecimal principalPortion,
                             BigDecimal interestPortion, BigDecimal remainingBalance,
                             BigDecimal cumulativeInterest) {
        this.paymentNumber = paymentNumber;
        this.payment = payment;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.remainingBalance = remainingBalance;
        this.cumulativeInterest = cumulativeInterest;
    }

    public int getPaymentNumber() {
        return paymentNumber;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public BigDecimal getPrincipalPortion() {
        return principalPortion;
    }

    public BigDecimal getInterestPortion() {
        return interestPortion;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public BigDecimal getCumulativeInterest() {
        return cumulativeInterest;
    }

    @Override
    public String toString() {
        return String.format(
            "%4d  |  $%,12.2f  |  $%,12.2f  |  $%,12.2f  |  $%,12.2f  |  $%,12.2f",
            paymentNumber, payment, principalPortion, interestPortion,
            remainingBalance, cumulativeInterest);
    }
}
