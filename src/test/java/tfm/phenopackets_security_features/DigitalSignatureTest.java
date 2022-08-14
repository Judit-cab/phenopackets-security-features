package tfm.phenopackets_security_features;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.SecurePhenopacket;
import phenopackets.examples.Covid19;
import phenopackets.securityMechanisms.DigitalSignature;

public class DigitalSignatureTest {

    Covid19 covidCase = new Covid19();

    @Test
    void checkDigitalSignature() throws IOException, URISyntaxException, ParseException, GeneralSecurityException{
        
        Phenopacket covidPhenopacket = covidCase.covid19Phenopacket();

        String phenopacketId = covidPhenopacket.getId();

        System.out.println("The unique identifiers is :" + phenopacketId);
        
        // Function that signs the Phenopacket and saves the signature to a file
        SecurePhenopacket.signPhenopacket(covidPhenopacket);
        System.out.println("Phenopacket successfully signed");

        // Get the signature and Phenopacket from file
        byte[] phenopacketBytes = SecurePhenopacket.getPhenopacketFromFile(phenopacketId);

        // Method to verify the signature, returns True if the verification is correct
        Boolean isVerfied = DigitalSignature.searchSignatureAndVerify(phenopacketBytes, phenopacketId);

        Assertions.assertTrue(isVerfied, "Signature cannot be verified");
    }
    
}