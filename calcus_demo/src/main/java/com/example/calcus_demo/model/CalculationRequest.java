package com.example.calcus_demo.model;


import jakarta.validation.constraints.NotNull;

public class CalculationRequest {
    @NotNull(message = "First number is required")
    private Double num1;

    @NotNull(message = "Second number is required")
    private Double num2;

    @NotNull(message = "Operation is required")
    private String operation;

    private Double result;
    private String error;

    // Getters and Setters
    public Double getNum1() { return num1; }
    public void setNum1(Double num1) { this.num1 = num1; }

    public Double getNum2() { return num2; }
    public void setNum2(Double num2) { this.num2 = num2; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Double getResult() { return result; }
    public void setResult(Double result) { this.result = result; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}