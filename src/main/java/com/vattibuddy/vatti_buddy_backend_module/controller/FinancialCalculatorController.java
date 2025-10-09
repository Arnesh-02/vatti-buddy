package com.vattibuddy.vatti_buddy_backend_module.controller;


import com.vattibuddy.vatti_buddy_backend_module.model.EmiCalculationResult;
import com.vattibuddy.vatti_buddy_backend_module.model.GoldPurityLevels;
import com.vattibuddy.vatti_buddy_backend_module.model.SilverPurityLevels;
import com.vattibuddy.vatti_buddy_backend_module.service.FinancialCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping("/calc/")
public class FinancialCalculatorController {


    FinancialCalculationService financialCalculationService;

    public FinancialCalculatorController(@Autowired FinancialCalculationService financialCalculationService) {
        this.financialCalculationService = financialCalculationService;
    }

    @GetMapping("/emiInterestRate")
    public ResponseEntity<EmiCalculationResult> calculateEmiInterestRate(double principal, double interestRate, int months){
        return new ResponseEntity<>(financialCalculationService.calculateEmiInterestRate(principal,interestRate,months), HttpStatus.OK);
    }

    @GetMapping("metals/getInterestRate")
    public ResponseEntity<EmiCalculationResult> calculateGoldInterestRate(double principal, double interestRate, String borrowDate, String returnDate) throws ParseException {
       DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
       LocalDate borrowD = LocalDate.parse(borrowDate,dateFormat);
       LocalDate returnD = LocalDate.parse(returnDate,dateFormat);
       return new ResponseEntity<>(financialCalculationService.calculateMetalInterestRate(principal,interestRate,borrowD,returnD),HttpStatus.OK);
    }
    @GetMapping("/maximumGoldLoan")
    public ResponseEntity<Double> calculatePossibleGoldLoan(double goldWeightInGrams, GoldPurityLevels goldPurityLevel){
        return new ResponseEntity<>(financialCalculationService.calculatePossibleGoldLoan(goldWeightInGrams,goldPurityLevel),HttpStatus.OK);
    }

    @GetMapping("/maximumSilverLoan")
    public ResponseEntity<Double> calculatePossibleSilverLoan(double silverWeightInGrams, SilverPurityLevels silverPurityLevel){
        return new ResponseEntity<>(financialCalculationService.calculatePossibleSilverLoan(silverWeightInGrams,silverPurityLevel),HttpStatus.OK);
    }
}
