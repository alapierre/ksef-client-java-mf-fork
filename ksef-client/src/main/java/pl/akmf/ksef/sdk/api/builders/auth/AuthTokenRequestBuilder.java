package pl.akmf.ksef.sdk.api.builders.auth;

import org.apache.commons.lang3.StringUtils;
import pl.akmf.ksef.sdk.client.model.xml.AuthTokenRequest;
import pl.akmf.ksef.sdk.client.model.xml.IpChangePolicyEnum;
import pl.akmf.ksef.sdk.client.model.xml.SubjectIdentifierTypeEnum;
import pl.akmf.ksef.sdk.client.model.xml.TContextIdentifier;

import java.util.List;

public class AuthTokenRequestBuilder {
    private String challenge;
    private boolean challengeSet = false;
    private final TContextIdentifier context = new TContextIdentifier();
    private SubjectIdentifierTypeEnum subjectIdentifierTypeEnum;
    private AuthTokenRequest.IpAddressPolicy ipPolicy;

    public AuthTokenRequestBuilder withChallenge(String challenge) {
        if (challenge == null || challenge.trim().isEmpty()) {
            throw new IllegalArgumentException("Challenge cannot be null or empty.");
        }

        this.challenge = challenge;
        this.challengeSet = true;
        return this;
    }

    public AuthTokenRequestBuilder withContextNip(String value) {
        if (!challengeSet) {
            throw new IllegalStateException("You must call withChallenge() before withContext().");
        }

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Context value cannot be null or empty.");
        }

        if (StringUtils.isNotBlank(context.getInternalId()) || StringUtils.isNotBlank(context.getNipVatUe())) {
            throw new IllegalArgumentException("Other context type has beem already set");
        }

        this.context.setNip(value);
        return this;
    }

    public AuthTokenRequestBuilder withInternalId(String value) {
        if (!challengeSet) {
            throw new IllegalStateException("You must call withChallenge() before withContext().");
        }

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Context value cannot be null or empty.");
        }

        if (StringUtils.isNotBlank(context.getNip()) || StringUtils.isNotBlank(context.getNipVatUe())) {
            throw new IllegalArgumentException("Other context type has beem already set");
        }

        this.context.setInternalId(value);
        return this;
    }

    public AuthTokenRequestBuilder withNipVatEu(String value) {
        if (!challengeSet) {
            throw new IllegalStateException("You must call withChallenge() before withContext().");
        }

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Context value cannot be null or empty.");
        }

        if (StringUtils.isNotBlank(context.getInternalId()) || StringUtils.isNotBlank(context.getNip())) {
            throw new IllegalArgumentException("Other context type has beem already set");
        }

        this.context.setNipVatUe(value);
        return this;
    }

    public AuthTokenRequestBuilder withSubjectType(SubjectIdentifierTypeEnum value) {
        if (!challengeSet) {
            throw new IllegalStateException("You must call withChallenge() before withSubject().");
        }

        this.subjectIdentifierTypeEnum = value;
        return this;
    }

    public AuthTokenRequestBuilder withIpAddressPolicy(String ipPolicyChanges,
                                                       List<String> ipAddress,
                                                       List<String> ipRange,
                                                       List<String> ipMask) {
        AuthTokenRequest.IpAddressPolicy.AllowedIps allowedIps = new AuthTokenRequest.IpAddressPolicy.AllowedIps();
        allowedIps.withIpAddress(ipAddress);
        allowedIps.withIpRange(ipRange);
        allowedIps.withIpMask(ipMask);

        this.ipPolicy = new AuthTokenRequest.IpAddressPolicy();
        this.ipPolicy.setOnClientIpChange(IpChangePolicyEnum.fromValue(ipPolicyChanges));
        this.ipPolicy.setAllowedIps(allowedIps);
        return this;
    }

    public AuthTokenRequest build() {
        if (!challengeSet) {
            throw new IllegalStateException("Challenge has not been set. Call withChallenge() first.");
        }

        AuthTokenRequest request = new AuthTokenRequest();
        request.setChallenge(challenge);
        request.setContextIdentifier(context);
        request.setSubjectIdentifierType(subjectIdentifierTypeEnum);
        request.setIpAddressPolicy(ipPolicy);
        return request;
    }
}
