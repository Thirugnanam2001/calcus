package com.example.calcus_demo.controllerTest;

import com.example.calcus_demo.controller.CalculatorController;
import com.example.calcus_demo.model.CalculationRequest;
import com.example.calcus_demo.service.CalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculatorService calculatorService;

    @Autowired
    private ObjectMapper objectMapper;

    private CalculationRequest calculationRequest;

    @BeforeEach
    void setUp() {
        calculationRequest = new CalculationRequest();
        calculationRequest.setNum1(10.0);
        calculationRequest.setNum2(5.0);
        calculationRequest.setOperation("add");
    }

    @Test
    void testShowCalculatorPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("calculationRequest"));
    }

    @Test
    void testCalculateEndpoint() throws Exception {
        when(calculatorService.calculate(anyDouble(), anyDouble(), anyString()))
                .thenReturn(15.0);

        mockMvc.perform(post("/calculate")
                        .param("num1", "10")
                        .param("num2", "5")
                        .param("operation", "add"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testCalculateApiEndpoint() throws Exception {
        when(calculatorService.calculate(anyDouble(), anyDouble(), anyString()))
                .thenReturn(15.0);

        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calculationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value(15.0));
    }

    @Test
    void testCalculateApiWithError() throws Exception {
        when(calculatorService.calculate(anyDouble(), anyDouble(), anyString()))
                .thenThrow(new ArithmeticException("Division by zero"));

        calculationRequest.setOperation("divide");

        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calculationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }
}