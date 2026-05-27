package com.financialservices.mortgage;

import java.math.BigDecimal;

/**
 * Immutable value object representing the input parameters for a mortgage loan.
 */
public final class LoanParameters {

    private final BigDecimal principal;
    private final BigDecimal annualInterestRate;
    private final int termInYears;
    private final BigDecimal downPayment;
    private final BigDecimal annualPropertyTax;
    private final BigDecimal annualInsurance;

    private LoanParameters(Builder builder) {
        this.principal = builder.principal;
        this.annualInterestRate = builder.annualInterestRate;
        this.termInYears = builder.termInYears;
        this.downPayment = builder.downPayment;
        this.annualPropertyTax = builder.annualPropertyTax;
        this.annualInsurance = builder.annualInsurance;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getLoanAmount() {
        return principal.subtract(downPayment);
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public int getTermInYears() {
        return termInYears;
    }

    public int getTermInMonths() {
        return termInYears * 12;
    }

    public BigDecimal getDownPayment() {
        return downPayment;
    }

    public BigDecimal getAnnualPropertyTax() {
        return annualPropertyTax;
    }

    public BigDecimal getAnnualInsurance() {
        return annualInsurance;
    }

    public BigDecimal getMonthlyPropertyTax() {
        return annualPropertyTax.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getMonthlyInsurance() {
        return annualInsurance.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public String toString() {
        return String.format(
            "LoanParameters{principal=%s, rate=%s%%, term=%d years, downPayment=%s}",
            principal, annualInterestRate, termInYears, downPayment);
    }

    public static class Builder {
        private BigDecimal principal;
        private BigDecimal annualInterestRate;
        private int termInYears;
        private BigDecimal downPayment = BigDecimal.ZERO;
        private BigDecimal annualPropertyTax = BigDecimal.ZERO;
        private BigDecimal annualInsurance = BigDecimal.ZERO;

        public Builder principal(BigDecimal principal) {
            this.principal = principal;
            return this;
        }

        public Builder principal(double principal) {
            this.principal = BigDecimal.valueOf(principal);
            return this;
        }

        public Builder annualInterestRate(BigDecimal rate) {
            this.annualInterestRate = rate;
            return this;
        }

        public Builder annualInterestRate(double rate) {
            this.annualInterestRate = BigDecimal.valueOf(rate);
            return this;
        }

        public Builder termInYears(int years) {
            this.termInYears = years;
            return this;
        }

        public Builder downPayment(BigDecimal downPayment) {
            this.downPayment = downPayment;
            return this;
        }

        public Builder downPayment(double downPayment) {
            this.downPayment = BigDecimal.valueOf(downPayment);
            return this;
        }

        public Builder annualPropertyTax(BigDecimal tax) {
            this.annualPropertyTax = tax;
            return this;
        }

        public Builder annualPropertyTax(double tax) {
            this.annualPropertyTax = BigDecimal.valueOf(tax);
            return this;
        }

        public Builder annualInsurance(BigDecimal insurance) {
            this.annualInsurance = insurance;
            return this;
        }

        public Builder annualInsurance(double insurance) {
            this.annualInsurance = BigDecimal.valueOf(insurance);
            return this;
        }

        public LoanParameters build() {
            validate();
            return new LoanParameters(this);
        }

        private void validate() {
            if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Principal must be a positive amount");
            }
            if (annualInterestRate == null || annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Annual interest rate must be non-negative");
            }
            if (annualInterestRate.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Annual interest rate must not exceed 100%");
            }
            if (termInYears <= 0) {
                throw new IllegalArgumentException("Loan term must be at least 1 year");
            }
            if (termInYears > 50) {
                throw new IllegalArgumentException("Loan term must not exceed 50 years");
            }
            if (downPayment.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Down payment must be non-negative");
            }
            if (downPayment.compareTo(principal) >= 0) {
                throw new IllegalArgumentException("Down payment must be less than principal");
            }
            if (annualPropertyTax.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Property tax must be non-negative");
            }
            if (annualInsurance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Insurance must be non-negative");
            }
        }
    }
}
