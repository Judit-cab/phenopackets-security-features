package tfm.phenopackets_security_features;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.core.Age;
import org.phenopackets.secure.schema.core.Evidence;
import org.phenopackets.secure.schema.core.OntologyClass;
import org.phenopackets.secure.schema.core.PhenotypicFeature;
import org.phenopackets.secure.schema.core.TimeElement;

import phenopackets.ElementsBuilder;
import phenopackets.blocksBuilder;
import phenopackets.securityFeatures.HybridEncryption;

public class HybridEncryptionTest {

    @Test
    void checkEncryption() throws IOException, GeneralSecurityException, URISyntaxException{
        String plaintText = "What we want to encrypt with hybrid encryption";
        String context = "Test for encryption";
        byte[] plainTextBytes = plaintText.getBytes();
        byte[] contextBytes = context.getBytes(); 

        byte[] cipher = HybridEncryption.hybridEncryption("encrypt", plainTextBytes, contextBytes);

        byte[] plainText = HybridEncryption.hybridEncryption("decrypt", cipher, contextBytes);

        String result = new String(plainText);
        
        Assertions.assertEquals(plaintText, result);
    }

    @Test
    void checkPhenotypicElement() throws IOException, GeneralSecurityException, URISyntaxException{
        OntologyClass type = blocksBuilder.ontologyClass("id", "label");
        OntologyClass severity = blocksBuilder.ontologyClass("id", "label");
        List<Evidence> evidence =  new ArrayList<Evidence>();
        Age age = Age.newBuilder().setIso8601Duration("isoAge").build();
        TimeElement element = TimeElement.newBuilder().setAge(age).build();

        PhenotypicFeature phenotypic = ElementsBuilder.phenotypicFeature(type, severity, evidence, element, element);

        String context = "Test for phenotypic feature element";

        byte[] plainTextBytes = phenotypic.toByteArray();
        byte[] contextBytes = context.getBytes(); 

        byte[] cipher = HybridEncryption.hybridEncryption("encrypt", plainTextBytes, contextBytes);

        byte[] plainText = HybridEncryption.hybridEncryption("decrypt", cipher, contextBytes);

        //String result = new String(plainText);
        PhenotypicFeature result = PhenotypicFeature.parseFrom(plainText);
        System.out.println("This is the original:" + phenotypic);

        System.out.println("This is the element after decryption:" + result);

        Assertions.assertEquals(phenotypic,result);

    }
}
