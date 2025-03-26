package com.silentowl.banking_app.kyc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KycVerificationService {

    @Value("${app.kyc.min-age}")
    private int MIN_AGE;

    @Value("${app.kyc.min-initial-deposit}")
    private BigDecimal MIN_INITIAL_DEPOSIT;

    @Value("${app.kyc.min-required-annual-income}")
    private BigDecimal MIN_REQUIRED_ANNUAL_INCOME_VALUE;

    @Value("${app.kyc.max-risk-annual-income}")
    private BigDecimal MAX_RISK_ANNUAL_INCOME;

    @Value("${app.kyc.lowest-risk-score}")
    private int LOWEST_RISK_SCORE;
    @Value("${app.kyc.medium-risk-score}")
    private int MEDIUM_RISK_SCORE;

    @Value("${app.kyc.min-high-risk-age}")
    private int MIN_HIGH_RISK_AGE;
    @Value("${app.kyc.max-high-risk-age}")
    private int MAX_HIGH_RISK_AGE;

    @Value("${app.kyc.occupation-risk.high-risk}")
    private String HIGH_RISK_OCCUPATIONS;


    public KycVerificationResponse verifyCustomer(KycVerificationRequest request) {

        //basic validation checks
        boolean isValidAge = validateAge(request.getDateOfBirth());
        boolean isValidInitialDeposit = validateInitialDeposit(request.getInitialDeposit());
        boolean isValidIncome = validateIncome(request.getAnnualIncome());

        // determine risk level and verification status
        RiskLevel riskLevel = determineRiskLevel(request);
        boolean isVerified = isValidAge && isValidInitialDeposit && isValidIncome;

        return KycVerificationResponse.builder()
                .verified(isVerified)
                .riskLevel(riskLevel)
                .rejectionReasons(collectRejectionReasons(isValidAge, isValidInitialDeposit, isValidIncome))
                .build();
    }


    private boolean validateAge(LocalDate dateOfBirth) {
        return dateOfBirth != null && Period.between(dateOfBirth, LocalDate.now()).getYears() >= MIN_AGE;
    }

    private boolean validateInitialDeposit(BigDecimal initialDeposit) {
        return initialDeposit != null && initialDeposit.compareTo(MIN_INITIAL_DEPOSIT) >= 0;
    }


    private boolean validateIncome(BigDecimal annualIncome) {
        return annualIncome != null && annualIncome.compareTo(MIN_REQUIRED_ANNUAL_INCOME_VALUE) >= 0;
    }


    private List<String> collectRejectionReasons(boolean isValidAge, boolean initialDeposit, boolean isValidIncome) {
        List<String> reasons = new ArrayList<>();
        if (!isValidAge) reasons.add("Customer is under 18 years");
        if (!initialDeposit) reasons.add("Initial deposit is below the minimum required amount");
        if (!isValidIncome) reasons.add("Insufficient annual income");

        return reasons;
    }


    private RiskLevel determineRiskLevel(KycVerificationRequest request) {
        int riskScore = calculateRiskScore(request);

        if (riskScore <= LOWEST_RISK_SCORE) return RiskLevel.LOW;
        if (riskScore <= MEDIUM_RISK_SCORE) return RiskLevel.MEDIUM;
        return RiskLevel.HIGH;
    }


    private int calculateRiskScore(KycVerificationRequest request) {
        int score = 0;

        // Age risk
        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < MIN_HIGH_RISK_AGE || age > MAX_HIGH_RISK_AGE) score += 30;
        // Income risk
        if (request.getAnnualIncome().compareTo(MAX_RISK_ANNUAL_INCOME) < 0) score += 20;
        // Occupation risk
        score += getRiskByOccupation(request.getOccupation());

        return score;
    }


    private int getRiskByOccupation(String occupation) {
        Set<String> highRiskOccupations = Arrays.stream(HIGH_RISK_OCCUPATIONS.split(", "))
                .map(String::trim)
                .collect(Collectors.toSet());
        return highRiskOccupations.contains(occupation) ? 40 : 10;
    }

}