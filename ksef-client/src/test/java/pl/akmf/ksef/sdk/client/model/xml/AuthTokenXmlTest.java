package pl.akmf.ksef.sdk.client.model.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.MarshalException;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.validation.SchemaFactory;
import java.util.List;

import static org.junit.Assert.*;

public class AuthTokenXmlTest {
    private static final String NAMESPACE = "http://ksef.mf.gov.pl/auth/token/2.0";

    @Test
    public void generatedFluentModelProducesSchemaValidAuthenticationXml() throws Exception {
        var request = request();
        request.withAuthorizationPolicy(new AuthTokenRequest.AuthorizationPolicy().withAllowedIps(
                new AuthTokenRequest.AuthorizationPolicy.AllowedIps()
                        .withIp4Address("192.0.2.1")
                        .withIp4Address(List.of("192.0.2.2"))
                        .withIp4Range("192.0.2.3-192.0.2.4")
                        .withIp4Mask("192.0.2.0/24")));
        Document xml = marshal(request);
        assertEquals("AuthTokenRequest", xml.getDocumentElement().getLocalName());
        assertEquals(NAMESPACE, xml.getDocumentElement().getNamespaceURI());
        assertEquals("1111111111", xml.getElementsByTagNameNS(NAMESPACE, "Nip").item(0).getTextContent());
        assertEquals("certificateSubject", xml.getElementsByTagNameNS(NAMESPACE, "SubjectIdentifierType").item(0).getTextContent());
        assertEquals(2, xml.getElementsByTagNameNS(NAMESPACE, "Ip4Address").getLength());
        var restored = (AuthTokenRequest) JAXBContext.newInstance(AuthTokenRequest.class)
                .createUnmarshaller().unmarshal(xml);
        assertEquals(request.getChallenge(), restored.getChallenge());
        assertEquals(List.of("192.0.2.1", "192.0.2.2"),
                restored.getAuthorizationPolicy().getAllowedIps().getIp4Address());
    }

    @Test(expected = MarshalException.class)
    public void schemaRejectsMultipleContextIdentifierChoices() throws Exception {
        var request = request();
        request.getContextIdentifier().withPeppolId("PPL123456");
        marshal(request);
    }

    @Test
    public void legacyIpChangePolicyRemainsAvailable() {
        assertEquals("ignore", IpChangePolicyEnum.IGNORE.value());
        assertEquals(IpChangePolicyEnum.REJECT, IpChangePolicyEnum.fromValue("reject"));
    }

    private AuthTokenRequest request() {
        return new ObjectFactory().createAuthTokenRequest()
                .withChallenge("20261108-CR-0123456789-ABCDEF0123-AB")
                .withContextIdentifier(new TContextIdentifier().withNip("1111111111"))
                .withSubjectIdentifierType(SubjectIdentifierTypeEnum.CERTIFICATE_SUBJECT);
    }

    private Document marshal(AuthTokenRequest request) throws Exception {
        var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        var schema = schemaFactory.newSchema(getClass().getResource("/xsd/AuthTokenRequest.xsd"));
        var marshaller = JAXBContext.newInstance(AuthTokenRequest.class).createMarshaller();
        marshaller.setSchema(schema);
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().newDocument();
        marshaller.marshal(request, new DOMResult(document));
        return document;
    }
}
