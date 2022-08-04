package tfm.phenopackets_security_features;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.TimeElement;
import org.phenopackets.secure.schema.core.Update;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.MainElements;
import phenopackets.SecurePhenopacket;
import phenopackets.securityFeatures.HybridEncryption;
import phenopackets.BlockBuilder;

public class HybridEncryptionTest {

    @Test
    void checkHybridEncryption() throws IOException, GeneralSecurityException, URISyntaxException, ParseException{
        String plaintText = "What we want to encrypt with hybrid encryption";
        String context = "Test for encryption";
        byte[] plainTextBytes = plaintText.getBytes();
        byte[] contextBytes = context.getBytes(); 

        byte[] cipher = HybridEncryption.hybridEncryption("encrypt", plainTextBytes, contextBytes);

        byte[] plainText = HybridEncryption.hybridEncryption("decrypt", cipher, contextBytes);

        String result = new String(plainText);
        
        Assertions.assertEquals(plaintText, result);
        HybridEncryption.saveInFile(cipher, "test4", "test1");
    }
 
    @Test
    void checkAge() throws IOException, GeneralSecurityException, URISyntaxException { 
        // using covid-19 example
        String isoAge = "P70Y";
        String id = SecurePhenopacket.generatePhenopacketId();

        TimeElement age = BlockBuilder.createTimeElementAge(isoAge.getBytes(), id.getBytes());

        System.out.println(age);
    
        String plainAge = BlockBuilder.getAge(age, id.getBytes());
        
        System.out.println(plainAge);

        Assertions.assertEquals(isoAge, plainAge);

    }

    @Test
    void checkMetaDataEncyption() throws IOException, GeneralSecurityException, URISyntaxException{
        
        //ONCOLOGY EXAMPLE 
        
        String phenopacketID = SecurePhenopacket.generatePhenopacketId();
        List<Resource> resources =  new ArrayList<Resource>();
        List<Update> updates = new ArrayList<Update>();
        
        TimeElement timeStamp = BlockBuilder.creaTimeElementTimestamp("20210511");
  
        Resource resource1 = BlockBuilder.createResource("hp", "human phenotype ontology", "HP", "http://purl.obolibrary.org/obo/hp.owl", "2018-03-08", "http://purl.obolibrary.org/obo/HP_");
        Resource resource2 = BlockBuilder.createResource("uberon", "uber anatomy ontology", "UBERON", "http://purl.obolibrary.org/obo/uberon.owl", "2019-03-08", "http://purl.obolibrary.org/obo/UBERON_");
        resources.add(resource1);
        resources.add(resource2);
        
        Update update = Update.newBuilder().setTimestamp(timeStamp.getTimestamp()).build();
        updates.add(update);

        MetaData metaDataTest = MainElements.createMetaData(timeStamp.getTimestamp(), "Peter R.", "Peter R.", resources, updates, "2.0");
        
        byte[] cipherMetadata = MainElements.protectedMetaData(metaDataTest, phenopacketID.getBytes());
        
        System.out.println(cipherMetadata);
        
        MetaData plainMetaData = MainElements.getMetaData(cipherMetadata, phenopacketID.getBytes());

        Assertions.assertEquals(metaDataTest, plainMetaData);

    }
}