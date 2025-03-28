package com.silentowl.banking_app.account;

import com.silentowl.banking_app.kyc.KycVerificationRequest;
import com.silentowl.banking_app.kyc.KycVerificationResponse;
import com.silentowl.banking_app.kyc.KycVerificationService;
import com.silentowl.banking_app.kyc.KycVerificationStatus;
import com.silentowl.banking_app.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOrchestrationService {

    private final KycVerificationService kycVerificationService;
    private final UserService userService;
    private final AccountService accountService;

    @Transactional
    public AccountCreationResponse createAccountWithKycVerification(AccountCreationRequest accountCreationRequest) {
        // 1. Perform KYC Verification
        KycVerificationRequest kycVerificationRequest = buildKycVerificationRequest(accountCreationRequest);
        KycVerificationResponse kycVerificationResponse = kycVerificationService.verifyCustomer(kycVerificationRequest);
        if (!kycVerificationResponse.isVerified()) {
            throw new IllegalArgumentException("User is not eligible to create an account on their own." + kycVerificationResponse.getRejectionReasons());
        }
        log.info("Customer Risk level: {}", kycVerificationResponse.getRiskLevel());

        // 2. Determine Customer Tier based on KYC Result
        CustomerTier customerTier = determineCustomerTier(kycVerificationResponse);
        log.info("Customer Tier: {}", customerTier);

        // 3. Create Customer with KYC status
        User customer = userService.createUser(
                accountCreationRequest,
                customerTier,
                mapKycStatus(kycVerificationResponse)
        );

        // 4. Create Account based on KYC verified Tier
        if (customer == null) throw new IllegalArgumentException("Customer was not created");
//        System.out.println(customerTier + " - " + accountCreationRequest.getUserRequest().getInitialDeposit());
        Account account = accountService.createAccount(
                customer,
                customerTier, // eg. GOLD, PLATINUM, SILVER, BASIC
                accountCreationRequest.getUserRequest().getInitialDeposit(),
                accountCreationRequest.getUserRequest().getAccountType()
        );

        return buildAccountCreationResponse(customer, account);
    }
    private KycVerificationRequest buildKycVerificationRequest(AccountCreationRequest request) {
        KycVerificationRequest kycRequest = new KycVerificationRequest();
        kycRequest.setFirstName(request.getUserRequest().getFirstName());
        kycRequest.setLastName(request.getUserRequest().getLastName());
        kycRequest.setDateOfBirth(request.getUserRequest().getDateOfBirth());
        kycRequest.setInitialDeposit(request.getUserRequest().getInitialDeposit());
        kycRequest.setAnnualIncome(request.getUserRequest().getAnnualIncome());
        kycRequest.setOccupation(request.getUserRequest().getOccupation());

        return kycRequest;
    }
    private CustomerTier determineCustomerTier(KycVerificationResponse kycVerificationResponse) {
        return switch (kycVerificationResponse.getRiskLevel()) {
            case LOW -> CustomerTier.PLATINUM;
            case MEDIUM -> CustomerTier.GOLD;
            case HIGH -> CustomerTier.SILVER;
        };
    }
    private KycVerificationStatus mapKycStatus(KycVerificationResponse kycVerificationResponse) {
        return kycVerificationResponse.isVerified()
                ? KycVerificationStatus.VERIFIED
                : KycVerificationStatus.REJECTED;
    }
    private AccountCreationResponse buildAccountCreationResponse(User customer, Account account) {
        AccountCreationResponse response = new AccountCreationResponse();
        // set personal info
        response.setCustomerId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());

        response.setCustomerTier(customer.getCustomerTier());
        response.setKycStatus(customer.getKycStatus());

        // set address details
        response.setStreet(customer.getStreet());
        response.setCity(customer.getCity());
        response.setState(customer.getState());
        response.setCountry(customer.getCountry());

        // set account details
        response.setAccountNumber(account.getIban());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setAccountStatus(account.getStatus());
        response.setCurrency(account.getCurrency());
        response.setCreatedAt(account.getCreatedDate());

        return response;
    }
}
