package com.example.calcus_demo.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {

    private static final int DECIMAL_PLACES = 10;

    public double add(double a, double b) {
        return round(round(a) + round(b));
    }

    public double subtract(double a, double b) {
        return round(round(a) - round(b));
    }

    public double multiply(double a, double b) {
        return round(round(a) * round(b));
    }

    public double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero!");
        }
        return round(round(a) / round(b));
    }

    public double percent(double a, double b) {
        // Calculate percentage: a% of b = (a * b) / 100
        // Or if used as pure percentage: a% = a/100
        return round((round(a) * round(b)) / 100);
    }

    public double modulo(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot perform modulo by zero!");
        }
        return round(round(a) % round(b));
    }

    private double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double calculate(double num1, double num2, String operation) {
        return switch (operation) {
            case "add" -> add(num1, num2);
            case "subtract" -> subtract(num1, num2);
            case "multiply" -> multiply(num1, num2);
            case "divide" -> divide(num1, num2);
            case "percent" -> percent(num1, num2);
            case "modulo" -> modulo(num1, num2);
            default -> throw new IllegalArgumentException("Invalid operation: " + operation);
        };
    }

    // Single operand operations
    public double calculateSingleOperand(double num, String operation) {
        return switch (operation) {
            case "percent" -> round(num / 100);
            case "square" -> round(num * num);
            case "sqrt" -> {
                if (num < 0) {
                    throw new ArithmeticException("Cannot calculate square root of negative number!");
                }
                yield round(Math.sqrt(num));
            }
            default -> throw new IllegalArgumentException("Invalid operation: " + operation);
        };
    }
}