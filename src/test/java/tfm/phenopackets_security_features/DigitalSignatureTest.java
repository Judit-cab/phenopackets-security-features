package tfm.phenopackets_security_features;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.SecurePhenopacket;
import phenopackets.Examples.Covid19;
import phenopackets.securityFeatures.DigitalSignature;

public class DigitalSignatureTest {

    Covid19 covidCase = new Covid19();

    @Test
    
    void checkDigitalSignature() throws IOException, URISyntaxException, ParseException, GeneralSecurityException{
        
        Phenopacket covidPhenopacket = covidCase.covid19Phenopacket();

        String phenopacketId = covidPhenopacket.getId();

        System.out.println("The unique identifiers is :" + phenopacketId);
        
        SecurePhenopacket.signPhenopacket(covidPhenopacket);
        System.out.println("Phenopacket successfully signed");

        try{
            SecurePhenopacket.verifyPhenopacket(covidPhenopacket);
            
        }catch(Exception ex){
            System.out.println(ex);
        }
        
    }

    @Test
    void verifyDigitalSignatureFromFile() throws URISyntaxException, IOException, GeneralSecurityException{
        String phenopacketId = "0fdf0d92-c1d0-4c89-b8c6-c3233db58496";
       
        byte[] phenopacketBytes = SecurePhenopacket.getPhenopacketFromFile(phenopacketId);

        Boolean isVerfied = DigitalSignature.searchSignatureAndVerify(phenopacketBytes, phenopacketId);

        assertTrue(isVerfied);
    }
    
}