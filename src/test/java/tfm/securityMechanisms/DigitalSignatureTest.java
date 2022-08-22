package tfm.securityMechanisms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.SecurePhenopacket;
import phenopackets.examples.Covid19;

public class DigitalSignatureTest {

    Covid19 covidCase = new Covid19();

    @Test
    void testDigitalSignature() throws IOException, URISyntaxException, ParseException, GeneralSecurityException{
        
        Phenopacket covidPhenopacket = covidCase.covid19Phenopacket();
        String phenopacketId = covidPhenopacket.getId();

        System.out.println("The unique identifier is :" + phenopacketId);
        
        SecurePhenopacket.signPhenopacket(covidPhenopacket);
        SecurePhenopacket.verifyPhenopacket(covidPhenopacket);

    }   
}