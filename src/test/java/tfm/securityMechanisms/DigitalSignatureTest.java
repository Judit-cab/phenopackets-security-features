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

    /*
     * TEST: Sign and verify Phenopacket schema
     * The values used can be found at covid19 class
     */
    @Test
    void testDigitalSignature() throws IOException, URISyntaxException, ParseException, GeneralSecurityException{
        
        Phenopacket covidPhenopacket = covidCase.covid19Phenopacket();
        String phenopacketId = covidPhenopacket.getId();

        System.out.println("The unique identifier is :" + phenopacketId);
        // Function that signs the Phenopacket and save the signature in a JSON file
        SecurePhenopacket.signPhenopacket(covidPhenopacket);
        // Function that retrieves the signature from the file and verifies it
        SecurePhenopacket.verifyPhenopacket(covidPhenopacket);

    }   
}