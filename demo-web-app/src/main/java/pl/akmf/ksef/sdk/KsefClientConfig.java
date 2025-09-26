package pl.akmf.ksef.sdk;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.akmf.ksef.sdk.api.services.DefaultCertificateService;
import pl.akmf.ksef.sdk.api.services.DefaultQrCodeService;
import pl.akmf.ksef.sdk.api.services.DefaultSignatureService;
import pl.akmf.ksef.sdk.api.services.DefaultVerificationLinkService;
import pl.akmf.ksef.sdk.client.interfaces.CertificateService;
import pl.akmf.ksef.sdk.client.interfaces.QrCodeService;
import pl.akmf.ksef.sdk.client.interfaces.SignatureService;
import pl.akmf.ksef.sdk.client.interfaces.VerificationLinkService;
import pl.akmf.ksef.sdk.util.ExampleApiProperties;

@Configuration
@RequiredArgsConstructor
public class KsefClientConfig {

    @Bean
    public ExampleApiProperties apiProperties() {
        return new ExampleApiProperties();
    }

    @Bean
    public CertificateService initDefaultCertificateService() {
        return new DefaultCertificateService();
    }

    @Bean
    public SignatureService initDefaultSignatureService() {
        return new DefaultSignatureService();
    }

    @Bean
    public VerificationLinkService initDefaultVerificationLinkService() {
        return new DefaultVerificationLinkService();
    }

    @Bean
    public QrCodeService initDefaultQrCodeService() {
        return new DefaultQrCodeService();
    }
}
