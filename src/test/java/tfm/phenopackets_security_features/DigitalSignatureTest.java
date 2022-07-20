package tfm.phenopackets_security_features;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.securityFeatures.DigitalSignature;

public class DigitalSignatureTest {

    @Test
    
    void checkSignature() throws IOException, URISyntaxException, ParseException{
        String mode = "sign";
        String elementName = "test3";
        String msg = "Sign this message";
        String mode2 ="verify";
        
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        JSONObject jsonObj = DigitalSignature.protectWithDS(mode, msgBytes, elementName);

        System.out.println(jsonObj);

        JSONObject jsonObj2 = DigitalSignature.protectWithDS(mode2, msgBytes, elementName);
        System.out.println(jsonObj2);
    }

    
}