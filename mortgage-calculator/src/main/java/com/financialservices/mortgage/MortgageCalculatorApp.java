package com.financialservices.mortgage;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Interactive CLI application for mortgage loan calculations.
 */
public class MortgageCalculatorApp {

    private final Scanner scanner;
    private final MortgageCalculator calculator;
    private final MortgageReportFormatter formatter;

    public MortgageCalculatorApp() {
        this.scanner = new Scanner(System.in);
        this.calculator = new MortgageCalculator();
        this.formatter = new MortgageReportFormatter();
    }

    public static void main(String[] args) {
        if (args.length >= 3) {
            runWithArgs(args);
        } else {
            new MortgageCalculatorApp().run();
        }
    }

    private static void runWithArgs(String[] args) {
        try {
            double principal = Double.parseDouble(args[0]);
            double rate = Double.parseDouble(args[1]);
            int term = Integer.parseInt(args[2]);
            double downPayment = args.length > 3 ? Double.parseDouble(args[3]) : 0;
            double propertyTax = args.length > 4 ? Double.parseDouble(args[4]) : 0;
            double insurance = args.length > 5 ? Double.parseDouble(args[5]) : 0;

            LoanParameters params = new LoanParameters.Builder()
                .principal(principal)
                .annualInterestRate(rate)
                .termInYears(term)
                .downPayment(downPayment)
                .annualPropertyTax(propertyTax)
                .annualInsurance(insurance)
                .build();

            MortgageCalculator calculator = new MortgageCalculator();
            MortgageReportFormatter formatter = new MortgageReportFormatter();
            MortgageSummary summary = calculator.calculate(params);

            System.out.println(formatter.formatSummary(summary));
            System.out.println(formatter.formatYearlySummary(summary));
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format. Usage:");
            printUsage();
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("  java -jar mortgage-calculator.jar <principal> <rate> <term_years> [down_payment] [property_tax] [insurance]");
        System.out.println();
        System.out.println("  Example: java -jar mortgage-calculator.jar 350000 6.5 30 70000 4200 1200");
    }

    public void run() {
        System.out.println("\n=============================================");
        System.out.println("   MORTGAGE LOAN CALCULATOR");
        System.out.println("   Financial Services Application");
        System.out.println("=============================================\n");

        boolean continueRunning = true;

        while (continueRunning) {
            try {
                LoanParameters params = collectInput();
                MortgageSummary summary = calculator.calculate(params);

                System.out.println(formatter.formatSummary(summary));

                if (promptYesNo("Would you like to see the yearly summary? (y/n): ")) {
                    System.out.println(formatter.formatYearlySummary(summary));
                }

                if (promptYesNo("Would you like to see the full amortization schedule? (y/n): ")) {
                    System.out.println(formatter.formatAmortizationSchedule(summary));
                }
            } catch (IllegalArgumentException e) {
                System.out.println("\nError: " + e.getMessage());
                System.out.println("Please try again.\n");
            }

            continueRunning = promptYesNo("\nCalculate another mortgage? (y/n): ");
        }

        System.out.println("\nThank you for using the Mortgage Loan Calculator!");
        scanner.close();
    }

    private LoanParameters collectInput() {
        System.out.println("Please enter the following loan details:\n");

        double principal = promptDouble("  Home price ($): ");
        double downPayment = promptDouble("  Down payment ($): ");
        double rate = promptDouble("  Annual interest rate (%): ");
        int term = promptInt("  Loan term (years): ");
        double propertyTax = promptDouble("  Annual property tax ($, 0 to skip): ");
        double insurance = promptDouble("  Annual homeowner's insurance ($, 0 to skip): ");

        return new LoanParameters.Builder()
            .principal(principal)
            .annualInterestRate(rate)
            .termInYears(term)
            .downPayment(downPayment)
            .annualPropertyTax(propertyTax)
            .annualInsurance(insurance)
            .build();
    }

    private double promptDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please try again.");
            }
        }
    }

    private int promptInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please try again.");
            }
        }
    }

    private boolean promptYesNo(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
}
