package com.vattibuddy.vatti_buddy_backend_module.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmiCalculationResult {
    private double principalAmount;
    private double monthlyRepaymentAmount;
    private double totalInterestAmount;
    private double totalRepaymentAmount;

    @Override
    public String toString() {
        return "EmiCalculationResult{" +
                "principalAmount=" + principalAmount +
                "monthlyRepaymentAmount=" + monthlyRepaymentAmount +
                ", totalInterestAmount=" + totalInterestAmount +
                ", totalRepaymentAmount=" + totalRepaymentAmount +
                '}';
    }
}
