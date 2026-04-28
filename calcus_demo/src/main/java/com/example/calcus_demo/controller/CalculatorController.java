package com.example.calcus_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.calcus_demo.model.CalculationRequest;
import com.example.calcus_demo.service.CalculatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CalculatorController {
    @GetMapping("/health")
    @ResponseBody
    public String healthCheck() {
        return "OK";
    }

    @Autowired
    private CalculatorService calculatorService;

    @GetMapping("/")
    public String showCalculator(Model model) {
        model.addAttribute("calculationRequest", new CalculationRequest());
        return "index";
    }

    @PostMapping("/calculate")
    public String calculate(@Valid @ModelAttribute CalculationRequest request, Model model) {
        try {
            double result = calculatorService.calculate(
                    request.getNum1(),
                    request.getNum2(),
                    request.getOperation()
            );
            request.setResult(result);
            model.addAttribute("calculationRequest", request);
        } catch (ArithmeticException e) {
            request.setError(e.getMessage());
            model.addAttribute("calculationRequest", request);
        } catch (Exception e) {
            request.setError("An error occurred: " + e.getMessage());
            model.addAttribute("calculationRequest", request);
        }

        return "index";
    }

    // REST API endpoint for AJAX calls with full operations
    @PostMapping("/api/calculate")
    @ResponseBody
    public CalculationResult calculateApi(@RequestBody CalculationRequest request) {
        CalculationResult result = new CalculationResult();
        try {
            double calculationResult;

            // Handle single operand operations
            if (request.getOperation().equals("percent_single")) {
                calculationResult = calculatorService.calculateSingleOperand(request.getNum1(), "percent");
            } else {
                calculationResult = calculatorService.calculate(
                        request.getNum1(),
                        request.getNum2(),
                        request.getOperation()
                );
            }

            result.setSuccess(true);
            result.setResult(calculationResult);
            result.setExpression(formatExpression(request));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(e.getMessage());
        }
        return result;
    }

    private String formatExpression(CalculationRequest request) {
        String symbol = switch (request.getOperation()) {
            case "add" -> "+";
            case "subtract" -> "-";
            case "multiply" -> "×";
            case "divide" -> "÷";
            case "percent" -> "% of";
            case "modulo" -> "mod";
            default -> request.getOperation();
        };
        return request.getNum1() + " " + symbol + " " + request.getNum2();
    }

    static class CalculationResult {
        private boolean success;
        private Double result;
        private String error;
        private String expression;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Double getResult() { return result; }
        public void setResult(Double result) { this.result = result; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
    }
}