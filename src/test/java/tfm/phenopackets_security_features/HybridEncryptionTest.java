package tfm.phenopackets_security_features;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.TimeElement;
import org.phenopackets.secure.schema.core.Update;
import org.phenopackets.secure.schema.core.VitalStatus;
import org.phenopackets.secure.schema.core.VitalStatus.Status;

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
    MetaData createMetada() throws IOException, GeneralSecurityException, URISyntaxException{
        
        //ONCOLOGY EXAMPLE 

        List<Resource> resources =  new ArrayList<Resource>();
        List<Update> updates = new ArrayList<Update>();
        
        TimeElement timeStamp = BlockBuilder.creaTimeElementTimestamp("2021-05-11T15:07:16.662Z");
  
        Resource resource1 = BlockBuilder.createResource("hp", "human phenotype ontology", "HP", "http://purl.obolibrary.org/obo/hp.owl", "2018-03-08", "http://purl.obolibrary.org/obo/HP_");
        Resource resource2 = BlockBuilder.createResource("uberon", "uber anatomy ontology", "UBERON", "http://purl.obolibrary.org/obo/uberon.owl", "2019-03-08", "http://purl.obolibrary.org/obo/UBERON_");
        resources.add(resource1);
        resources.add(resource2);
        
        Update update = Update.newBuilder().setTimestamp(timeStamp.getTimestamp()).build();
        updates.add(update);

        MetaData metaDataTest = MainElements.createMetaData(timeStamp.getTimestamp(), "Peter R.", "Peter R.", resources, updates, "2.0");
        
        System.out.println(metaDataTest);
        
        return metaDataTest;

    }

    @Test 
    MetaData createMetadaProtectingCreator() throws IOException, GeneralSecurityException, URISyntaxException{
        
        //ONCOLOGY EXAMPLE
        String id = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c"; 

        List<Resource> resources =  new ArrayList<Resource>();
        List<Update> updates = new ArrayList<Update>();
        
        TimeElement timeStamp = BlockBuilder.creaTimeElementTimestamp("2021-05-11T15:07:16.662Z");
  
        Resource resource1 = BlockBuilder.createResource("hp", "human phenotype ontology", "HP", "http://purl.obolibrary.org/obo/hp.owl", "2018-03-08", "http://purl.obolibrary.org/obo/HP_");
        Resource resource2 = BlockBuilder.createResource("uberon", "uber anatomy ontology", "UBERON", "http://purl.obolibrary.org/obo/uberon.owl", "2019-03-08", "http://purl.obolibrary.org/obo/UBERON_");
        resources.add(resource1);
        resources.add(resource2);
        
        Update update = Update.newBuilder().setTimestamp(timeStamp.getTimestamp()).build();
        updates.add(update);

        MetaData metaData = MainElements.protectedMetaDataCreator(timeStamp.getTimestamp(), "Peter R.", "Peter R.", resources, updates, "2.0", id.getBytes());
        
        System.out.println(metaData);
        
        return metaData;
    }

    @Test
    TimeElement createAge() throws IOException, GeneralSecurityException, URISyntaxException{
        
        // COVID-19 example
        String isoAge = "P70Y";
        String id = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c";

        TimeElement age = BlockBuilder.createTimeElementAge(isoAge.getBytes(), id.getBytes());

        System.out.println("The value of the encrypted age is:");
        System.out.println(age);

        return age;

    }

    Individual createIndividual() throws IOException, GeneralSecurityException, URISyntaxException{
        TimeElement age = createAge();
        VitalStatus vitalStatus = VitalStatus.newBuilder().setStatus(Status.ALIVE).build();
        Individual individual = MainElements.createSubject(age, vitalStatus, 4);

        return individual;
    }

    @Test
    Phenopacket createPhenopacket() throws IOException, GeneralSecurityException, URISyntaxException{
        String id = SecurePhenopacket.generatePhenopacketId();
        Individual subject = createIndividual();
        MetaData metaData = createMetada();

        return SecurePhenopacket.createPhenopacketToHybrydEncryption(id, subject, metaData); 
    }


    @Test
    void checkAgeDecryption() throws IOException, GeneralSecurityException, URISyntaxException { 
        
        Phenopacket phenopacket = createPhenopacket();
        String phenopacketID = phenopacket.getId();
    
        TimeElement age = phenopacket.getSubject().getTimeAtLastEncounter();
        String plainAge = BlockBuilder.getAge(age, phenopacketID.getBytes());
        
        System.out.println("After decryption it gets the same age value:" + plainAge);
    }

    
    @Test
    void checkMetaDataEncyption() throws IOException, GeneralSecurityException, URISyntaxException{
        
        Phenopacket phenopacket = createPhenopacket();
        String phenopacketID = phenopacket.getId();

        MetaData metaData = phenopacket.getMetaData();
        
        byte[] cipherMetadata = MainElements.protectedMetaData(metaData, phenopacketID.getBytes());
        
        MetaData plainMetaData = MainElements.getMetaData(cipherMetadata, phenopacketID.getBytes());
        
        System.out.println("After decryption it gets the same metadata value:");
        System.out.println(plainMetaData);
        
        Assertions.assertEquals(metaData, plainMetaData);
    }

    @Test
    void savePhenopacket() throws IOException, GeneralSecurityException, URISyntaxException, ParseException{
        String phenopacketID = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c";
        Phenopacket phenopacket = createPhenopacket();

        byte[] phenopacketBytes = phenopacket.toByteArray();
        HybridEncryption.saveInFile(phenopacketBytes, "Phenopacket", phenopacketID);
    }

    @Test
    void saveMetaData() throws IOException, GeneralSecurityException, URISyntaxException, ParseException{
        Phenopacket phenopacket = createPhenopacket();
        SecurePhenopacket.protectMetaData(phenopacket);

    }

    @Test
    void getMetadataFromFile() throws URISyntaxException, IOException, GeneralSecurityException{
        String phenopacketID = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c";

        byte[] cipherMetadata = HybridEncryption.getCipherBytes("metaData", phenopacketID);

        MetaData plainMetaData = MainElements.getMetaData(cipherMetadata, phenopacketID.getBytes());
        
        System.out.println(plainMetaData);
    }

    @Test
    void getAndCheckAgeFromFile() throws URISyntaxException, IOException, GeneralSecurityException{
        String phenopacketID = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c";
        byte[] phenopacketBytes = HybridEncryption.getCipherBytes("Phenopacket", phenopacketID);

        Phenopacket phenopacket = Phenopacket.parseFrom(phenopacketBytes);

        TimeElement age = phenopacket.getSubject().getTimeAtLastEncounter();

        System.out.println("Stored age in phenopacket as:");
        System.out.println(age);

        String plainAge = BlockBuilder.getAge(age, phenopacketID.getBytes());
        
        System.out.println("After decryption it gets the same age value:" + plainAge);
    }


    @Test
    void randomTest() throws IOException, GeneralSecurityException, URISyntaxException{
        String phenopacketID = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c";
        
        //byte[] cipherMetadata = MainElements.protectedMetaData(metaData, phenopacketID.getBytes());

        Phenopacket pheno = createPhenopacket();
        MetaData metaData = pheno.getMetaData();
        pheno = Phenopacket.newBuilder().clearMetaData().build();
        Boolean res  = pheno.hasMetaData();
        System.out.println(res);
        
    }



}