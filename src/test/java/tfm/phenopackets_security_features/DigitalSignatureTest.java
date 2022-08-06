package tfm.phenopackets_security_features;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.SecurePhenopacket;
import phenopackets.Examples.Covid19;

public class DigitalSignatureTest {

    Covid19 covidCase = new Covid19();

    @Test
    
    void checkDigitalSignature() throws IOException, URISyntaxException, ParseException, GeneralSecurityException{
        
        Phenopacket covidPhenopacket = covidCase.covid19Phenopacket();

        String phenopacketID = covidPhenopacket.getId();

        System.out.println("The unique identifiers is :" + phenopacketID);
        
        try{
            SecurePhenopacket.signPhenopacket(covidPhenopacket);
            System.out.println("Phenopacket successfully signed");
        }catch(Exception ex){
            System.out.println(ex);
        }

        try{
            SecurePhenopacket.verifyPhenopacket(covidPhenopacket);
            System.out.println("Phenopacket successfully verified");
        }catch(Exception ex){
            System.out.println(ex);
        }
        
    }
    
}