package com.silentowl.banking_app.kyc;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KycVerificationResponse {
    private boolean verified;
    private RiskLevel riskLevel;
    private List<String> rejectionReasons;
}
