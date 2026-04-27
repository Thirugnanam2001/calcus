package com.example.calcus_demo.serviceTest;

import com.example.calcus_demo.service.CalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }

    @Test
    @DisplayName("Test Addition Operation")
    void testAddition() {
        assertEquals(5.0, calculatorService.add(2.0, 3.0));
        assertEquals(0.0, calculatorService.add(-2.0, 2.0));
        assertEquals(-5.0, calculatorService.add(-2.0, -3.0));
        assertEquals(10.5, calculatorService.add(5.2, 5.3));
    }

    @Test
    @DisplayName("Test Subtraction Operation")
    void testSubtraction() {
        assertEquals(1.0, calculatorService.subtract(4.0, 3.0));
        assertEquals(-4.0, calculatorService.subtract(2.0, 6.0));
        assertEquals(5.0, calculatorService.subtract(-2.0, -7.0));
    }

    @Test
    @DisplayName("Test Multiplication Operation")
    void testMultiplication() {
        assertEquals(15.0, calculatorService.multiply(3.0, 5.0));
        assertEquals(0.0, calculatorService.multiply(0.0, 5.0));
        assertEquals(-12.0, calculatorService.multiply(-3.0, 4.0));
    }

    @Test
    @DisplayName("Test Division Operation")
    void testDivision() {
        assertEquals(2.0, calculatorService.divide(10.0, 5.0));
        assertEquals(0.0, calculatorService.divide(0.0, 5.0));
        assertEquals(-2.0, calculatorService.divide(-10.0, 5.0));
    }

    @Test
    @DisplayName("Test Division by Zero")
    void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculatorService.divide(10.0, 0.0);
        });
    }

    @Test
    @DisplayName("Test Percentage Operation")
    void testPercentage() {
        assertEquals(10.0, calculatorService.percent(20.0, 50.0));
        assertEquals(30.0, calculatorService.percent(15.0, 200.0));
        assertEquals(0.0, calculatorService.percent(0.0, 100.0));
    }

    @Test
    @DisplayName("Test Modulo Operation")
    void testModulo() {
        assertEquals(1.0, calculatorService.modulo(10.0, 3.0));
        assertEquals(0.0, calculatorService.modulo(10.0, 2.0));
        assertThrows(ArithmeticException.class, () -> {
            calculatorService.modulo(10.0, 0.0);
        });
    }

    @ParameterizedTest
    @DisplayName("Test Multiple Calculation Scenarios")
    @CsvSource({
            "5, 3, add, 8",
            "10, 4, subtract, 6",
            "7, 6, multiply, 42",
            "20, 4, divide, 5",
            "25, 80, percent, 20"
    })
    void testCalculate(double num1, double num2, String operation, double expected) {
        assertEquals(expected, calculatorService.calculate(num1, num2, operation));
    }

    @Test
    @DisplayName("Test Single Operand Percentage")
    void testSingleOperandPercentage() {
        assertEquals(0.25, calculatorService.calculateSingleOperand(25.0, "percent"));
        assertEquals(0.50, calculatorService.calculateSingleOperand(50.0, "percent"));
        assertEquals(0.0, calculatorService.calculateSingleOperand(0.0, "percent"));
    }

    @Test
    @DisplayName("Test Square Operation")
    void testSquare() {
        assertEquals(25.0, calculatorService.calculateSingleOperand(5.0, "square"));
        assertEquals(0.0, calculatorService.calculateSingleOperand(0.0, "square"));
        assertEquals(2.25, calculatorService.calculateSingleOperand(1.5, "square"));
    }

    @Test
    @DisplayName("Test Square Root Operation")
    void testSquareRoot() {
        assertEquals(4.0, calculatorService.calculateSingleOperand(16.0, "sqrt"));
        assertEquals(0.0, calculatorService.calculateSingleOperand(0.0, "sqrt"));
        assertThrows(ArithmeticException.class, () -> {
            calculatorService.calculateSingleOperand(-4.0, "sqrt");
        });
    }
}