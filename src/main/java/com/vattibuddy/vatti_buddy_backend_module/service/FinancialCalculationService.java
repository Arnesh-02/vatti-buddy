package com.vattibuddy.vatti_buddy_backend_module.service;

import com.vattibuddy.vatti_buddy_backend_module.model.EmiCalculationResult;
import com.vattibuddy.vatti_buddy_backend_module.model.GoldPurityLevels;
import com.vattibuddy.vatti_buddy_backend_module.model.SilverPurityLevels;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class FinancialCalculationService {

    @Value("${api.key}")
    private String apiKey;


    public EmiCalculationResult calculateEmiInterestRate(double principal, double annualInterestRate,int months){
        EmiCalculationResult emiCalculationResult = new EmiCalculationResult();
        double monthlyInterestRate = annualInterestRate / (12 *100);
        double emi=((principal*monthlyInterestRate)*Math.pow(1+monthlyInterestRate,months))/(Math.pow(1+monthlyInterestRate,months)-1);
        emiCalculationResult.setPrincipalAmount(principal);
        emiCalculationResult.setMonthlyRepaymentAmount(emi);
        emiCalculationResult.setTotalInterestAmount(emi*months - principal);
        emiCalculationResult.setTotalRepaymentAmount(emi*months);
        return  emiCalculationResult;
    }

    public double getPurityFactorOfGold(GoldPurityLevels goldPurityLevel){
        if(goldPurityLevel.equals(GoldPurityLevels.K_22)){
            return  22/22.0;
        }
        else if(goldPurityLevel.equals(GoldPurityLevels.K_24)){
            return  22/24.0;
        }
        else if(goldPurityLevel.equals(GoldPurityLevels.K_20)){
            return  22/20.0;
        }
        else{
            return 22/18.0;
        }
    }

    public  double getGoldPriceRate(){
        RestTemplate restTemplate = new RestTemplate();
        String api_url="https://www.goldapi.io/api/XAU/INR";
        HttpHeaders headers=new HttpHeaders();
        headers.set("x-access-token",apiKey);

        HttpEntity<String> entity=new HttpEntity<>(headers);

        ResponseEntity<String> response=restTemplate.exchange(api_url, HttpMethod.GET,entity,String.class);
        System.out.println(response);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(response.getBody());
            return json.getDouble("price_gram_24k");
        } else {
            throw new RuntimeException("Failed to fetch gold rate. HTTP Code: " + response.getStatusCode());
        }
    }

    public double calculatePossibleGoldLoan(double goldWeightInGrams, GoldPurityLevels goldPurityLevel) {
        double goldFactor=getPurityFactorOfGold(goldPurityLevel);
        double  goldPriceRate=getGoldPriceRate();
        double goldValue=(goldWeightInGrams) * goldFactor* goldPriceRate;
        System.out.println("Gold value: " + goldValue + " Gold Price Rate: " + goldPriceRate +" Gold factor: " + goldFactor);
        double ltvRatio = 0.75;
        return goldValue * ltvRatio;
    }

    public double calculatePossibleSilverLoan(double silverWeightInGrams, SilverPurityLevels silverPurityLevel){
        double silverValue=silverWeightInGrams * getSilverPurityFactor(silverPurityLevel) * getSilverRate();
        double ltvRatio = 0.65;
        return silverValue * ltvRatio;
    }

    private double getSilverRate() {
        RestTemplate restTemplate = new RestTemplate();
        String url="https://www.goldapi.io/api/XAG/INR";
        HttpHeaders headers=new HttpHeaders();
        headers.set("x-access-token",apiKey);
        HttpEntity<String> entity=new HttpEntity<>(headers);
        ResponseEntity<String> response=restTemplate.exchange(url,HttpMethod.GET,entity,String.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonObject=new JSONObject(response);
            return jsonObject.getDouble("price_gram_24k");
        }
        else{
            throw new RuntimeException("Failed to fetch silver rate. HTTP Code: " + response.getStatusCode());
        }
    }

    private double getSilverPurityFactor(SilverPurityLevels silverPurityLevel) {
     if(silverPurityLevel.equals(SilverPurityLevels.S_999)) {
         return 1.0;
     }
     else if(silverPurityLevel.equals(SilverPurityLevels.S_925)) {
         return 925/999.0;
     }
     else{
         return 900/999.0;
     }
    }

    public EmiCalculationResult calculateMetalInterestRate(double principal, double monthlyInterestRate,LocalDate borrowDate,LocalDate returnDate) {

        Period timePeriod=Period.between(borrowDate,returnDate);
        int fullMonths= timePeriod.getYears()*12 + timePeriod.getMonths();
        LocalDate temp=borrowDate.plusMonths(fullMonths);
        int noOfRemainingDays=(int) Math.round(ChronoUnit.DAYS.between(temp,returnDate));
        double monthlyInterest=principal * (monthlyInterestRate/100);

        double dailyInterest=monthlyInterest/30;
        System.out.println(dailyInterest);
        double totalInterest=monthlyInterest * fullMonths+dailyInterest*noOfRemainingDays;
        EmiCalculationResult emiCalculationResult=new EmiCalculationResult();
        emiCalculationResult.setPrincipalAmount(principal);
        emiCalculationResult.setTotalInterestAmount(totalInterest);
        emiCalculationResult.setMonthlyRepaymentAmount(monthlyInterest);
        return emiCalculationResult;
    }
    }

